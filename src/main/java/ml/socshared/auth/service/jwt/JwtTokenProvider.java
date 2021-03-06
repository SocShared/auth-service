package ml.socshared.auth.service.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.model.SpringUserDetails;
import ml.socshared.auth.domain.request.ServiceTokenRequest;
import ml.socshared.auth.domain.response.ServiceTokenResponse;
import ml.socshared.auth.entity.*;
import ml.socshared.auth.exception.impl.AuthenticationException;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.repository.ServiceTokenRepository;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.service.SessionService;
import ml.socshared.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.auth.repository.SocsharedServiceRepository;
import ml.socshared.auth.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access_token.expired}")
    private long validityAccessTokenInMilliseconds;
    @Value("${jwt.refresh_token.expired}")
    private long validityRefreshTokenInMilliseconds;
    @Value("${jwt.service_token.expired}")
    private long validityServiceTokenInMilliseconds;
    @Value("${service.id}")
    private String serviceId;

    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final SocsharedServiceRepository socsharedServiceRepository;
    private final ServiceTokenRepository serviceTokenRepository;

    public OAuth2TokenResponse createTokenByUserAndClient(User user, Client client) {
        Session session = createSession(client, user);

        Claims claimsAccess = JwtClaimsBuilder.buildJwtClaimsByUserClientSession(user, client, session);
        String accessToken = generationToken(claimsAccess, validityAccessTokenInMilliseconds);

        String sessionId = claimsAccess.get("session_state", String.class);
        Claims claimsRefresh = Jwts.claims().setSubject(user.getUserId().toString());
        claimsRefresh.put("client_id", client.getClientId().toString());
        claimsRefresh.put("session_state", sessionId);
        String refreshToken = generationToken(claimsRefresh, validityRefreshTokenInMilliseconds);

        log.warn("session -> {}", session);
        OAuth2TokenResponse oAuth2TokenResponse = OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .expireIn(claimsAccess.getExpiration().getTime())
                .refreshToken(refreshToken)
                .sessionId(session.getSessionId().toString())
                .tokenType("bearer")
                .build();

        OAuth2AccessToken aToken = new OAuth2AccessToken();
        if (session.getAccessToken() != null)
            aToken.setTokenId(session.getAccessToken().getTokenId());
        aToken.setAccessToken(accessToken);
        aToken.setExpireIn(claimsAccess.getExpiration().getTime());
        aToken.setSession(session);
        aToken.setTokenType("bearer");
        session.setAccessToken(aToken);
        session.setOfflineSession(false);

        OAuth2RefreshToken rToken = new OAuth2RefreshToken();
        if (session.getRefreshToken() != null)
            rToken.setRefreshTokenId(session.getRefreshToken().getRefreshTokenId());
        rToken.setRefreshExpiresIn(claimsRefresh.getExpiration().getTime());
        rToken.setRefreshToken(refreshToken);
        session.setRefreshToken(rToken);
        session.setActiveSession(true);

        sessionService.save(session);

        return oAuth2TokenResponse;
    }

    public OAuth2TokenResponse createTokenByRefreshToken(String refreshToken, Client client) {
        Jws<Claims> refreshClaims = getJwsClaimsFromToken(refreshToken);
        UUID userId = UUID.fromString(refreshClaims.getBody().getSubject());
        User user = userRepository.findById(userId).orElse(null);

        if (user == null)
            throw new AuthenticationException("Invalid user");

        Session session = sessionService.findByClientIdAndUserId(client.getClientId(), userId);
        if (session == null)
            throw new AuthenticationException("Invalid session");

        Claims claimsAccess = JwtClaimsBuilder.buildJwtClaimsByUserClientSession(user, client, session);
        String accessToken = generationToken(claimsAccess, validityAccessTokenInMilliseconds);

        Claims claimsRefresh = refreshClaims.getBody();
        String newRefreshToken = generationToken(claimsRefresh, validityRefreshTokenInMilliseconds);

        OAuth2TokenResponse response = OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .expireIn(claimsAccess.getExpiration().getTime())
                .refreshToken(newRefreshToken)
                .sessionId(session.getSessionId().toString())
                .tokenType("bearer")
                .build();

        OAuth2AccessToken aToken = new OAuth2AccessToken();
        if (session.getAccessToken() != null)
            aToken.setTokenId(session.getAccessToken().getTokenId());
        aToken.setAccessToken(accessToken);
        aToken.setExpireIn(claimsAccess.getExpiration().getTime());
        aToken.setSession(session);
        aToken.setTokenType("bearer");
        session.setAccessToken(aToken);
        session.setOfflineSession(false);

        OAuth2RefreshToken rToken = new OAuth2RefreshToken();
        if (session.getRefreshToken() != null)
            rToken.setRefreshTokenId(session.getRefreshToken().getRefreshTokenId());
        rToken.setRefreshExpiresIn(claimsRefresh.getExpiration().getTime());
        rToken.setRefreshToken(newRefreshToken);
        session.setRefreshToken(rToken);
        session.setActiveSession(true);

        sessionService.save(session);

        return response;
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("username", String.class);
    }

    public UserDetails getUserDetails(String token) {
        Claims claims =  Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        ArrayList<String> roles = claims.get("roles", ArrayList.class);
        String roleService = claims.get("role", String.class);

        if (roles != null) {
            return SpringUserDetails.builder()
                    .authorities(roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList()))
                    .username(claims.get("username", String.class))
                    .firstName(claims.get("firstname", String.class))
                    .lastName(claims.get("lastname", String.class))
                    .email(claims.get("email", String.class))
                    .accountNonLocked(claims.get("account_non_locked", Boolean.class))
                    .build();
        } else if (roleService != null) {
            return SpringUserDetails.builder()
                    .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + roleService)))
                    .username(claims.get("from_service", String.class))
                    .accountNonLocked(true)
                    .build();
        }
        throw new AuthenticationException("invalid user details token");
    }

    public ServiceTokenResponse buildServiceToken(ServiceTokenRequest request) {
        Claims claimsAccess = Jwts.claims().setSubject(request.getFromServiceId().toString());
        claimsAccess.put("auth_time", new Date().getTime());
        claimsAccess.put("typ", "bearer");
        claimsAccess.put("from_service", request.getFromServiceId().toString());
        claimsAccess.put("to_service", request.getToServiceId().toString());
        claimsAccess.put("role", "SERVICE");

        Date now = new Date();
        Date expireIn = new Date(now.getTime() + validityServiceTokenInMilliseconds);
        JwtBuilder builder = Jwts.builder()
                .setClaims(claimsAccess)
                .setIssuedAt(now)
                .setExpiration(expireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey);

        ServiceToken token = serviceTokenRepository.findByToServiceIdAndFromServiceId(request.getToServiceId(), request.getFromServiceId())
                .orElse(new ServiceToken());
        token.setFromService(socsharedServiceRepository.findById(request.getFromServiceId())
                .orElseThrow(() -> new HttpNotFoundException("Not found service by id")));
        token.setToken(builder.compact());
        token.setTokenExpireIn(expireIn.getTime());
        token.setToServiceId(request.getToServiceId());

        serviceTokenRepository.save(token);

        return ServiceTokenResponse.builder()
                .expireIn(expireIn.getTime())
                .fromService(request.getFromServiceId().toString())
                .toService(request.getToServiceId().toString())
                .token(builder.compact())
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = getJwsClaimsFromToken(token);
            String toServiceId = claims.getBody().get("to_service", String.class);
            if (serviceId.equals(toServiceId)) {
                return validateServiceToken(token);
            } else {
                return false;
            }
        } catch (JwtException | IllegalArgumentException exc) {
            if (exc instanceof ExpiredJwtException) {
                log.warn("JWT Token is expired.");
            } else {
                log.warn("JWT Token is invalid.");
            }
        }
        return validateAccessToken(token);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = getJwsClaimsFromToken(token);
            UUID userId = UUID.fromString(claims.getBody().getSubject());
            UUID clientId = UUID.fromString(claims.getBody().get("client_id", String.class));
            Date date = claims.getBody().getExpiration();
            UUID sessionState = UUID.fromString(claims.getBody().get("session_state", String.class));
            Session session = sessionService.findById(sessionState);
            if (date.before(new Date()) && session.getUser().getUserId().equals(userId)
                    && session.getClient().getClientId().equals(clientId)) {
                log.warn("JWT Token is expired.");
                session.setActiveSession(false);
                sessionService.save(session);
                return false;
            }
            return session != null && session.getAccessToken() != null &&
                    session.getAccessToken().getAccessToken().equals(token);
        } catch (JwtException | IllegalArgumentException exc) {
            if (exc instanceof ExpiredJwtException) {
                log.warn("JWT Token is expired.");
            } else {
                log.warn("JWT Token is invalid.");
            }
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = getJwsClaimsFromToken(token);
            UUID userId = UUID.fromString(claims.getBody().getSubject());
            UUID clientId = UUID.fromString(claims.getBody().get("client_id", String.class));
            Date date = claims.getBody().getExpiration();
            UUID sessionState = UUID.fromString(claims.getBody().get("session_state", String.class));
            Session session = sessionService.findById(sessionState);
            if (date.before(new Date()) && session.getUser().getUserId().equals(userId)
                    && session.getClient().getClientId().equals(clientId)) {
                log.warn("JWT Token is expired.");
                session.setActiveSession(false);
                session.setOfflineSession(true);
                sessionService.save(session);
                return false;
            }
            log.info("refresh token session -> {}", session);
            return session != null && session.getRefreshToken() != null &&
                    session.getRefreshToken().getRefreshToken().equals(token);
        } catch (JwtException | IllegalArgumentException exc) {
            if (exc instanceof ExpiredJwtException) {
                log.warn("JWT Token is expired.");
            } else {
                log.warn("JWT Token is invalid.");
            }
            log.error(exc.getMessage());
            return false;
        }
    }

    public boolean validateServiceToken(String token) {
        try {
            Jws<Claims> claims = getJwsClaimsFromToken(token);

            Date date = claims.getBody().getExpiration();
            if (date.before(new Date())) {
                log.warn("JWT Token is expired.");
                return false;
            }
            UUID toService = UUID.fromString(claims.getBody().get("to_service", String.class));
            UUID fromService = UUID.fromString(claims.getBody().get("from_service", String.class));
            boolean isPresentToServiceId = socsharedServiceRepository.existsById(toService);
            boolean isPresentFromServiceId = socsharedServiceRepository.existsById(fromService);

            ServiceToken serviceToken = serviceTokenRepository.findByToServiceIdAndFromServiceId(toService, fromService).orElse(null);

            return isPresentToServiceId && isPresentFromServiceId && serviceToken != null
                    && serviceToken.getToken().equals(token);
        } catch (JwtException | IllegalArgumentException exc) {
            if (exc instanceof ExpiredJwtException) {
                log.warn("JWT Token is expired.");
            } else {
                log.warn("JWT Token is invalid.");
            }
            return false;
        }
    }

    public UUID getUserIdByToken(String token) {
        Jws<Claims> claims = getJwsClaimsFromToken(token);
        return UUID.fromString(claims.getBody().getSubject());
    }

    private String generationToken(Claims claims, long validityMilliseconds) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityMilliseconds))
                .signWith(SignatureAlgorithm.HS512, secretKey);
        return builder.compact();
    }

    private Jws<Claims> getJwsClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    }

    public Session createSession(Client client, User user) {
        Session session = sessionService.findByClientIdAndUserId(client.getClientId(), user.getUserId());
        if (session == null) {
            session = new Session();
            session.setSessionId(UUID.randomUUID());
            session.setClient(client);
            session.setUser(user);
            session.setActiveSession(false);
            session.setOfflineSession(true);
        }
        session.setActiveSession(true);
        return sessionService.save(session);
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            return token.substring(6).trim();
        }
        return null;
    }
}
