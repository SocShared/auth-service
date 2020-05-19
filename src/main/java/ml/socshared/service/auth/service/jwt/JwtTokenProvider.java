package ml.socshared.service.auth.service.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.model.SpringUserDetails;
import ml.socshared.service.auth.domain.request.ServiceTokenRequest;
import ml.socshared.service.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.service.auth.domain.response.ServiceTokenResponse;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.*;
import ml.socshared.service.auth.exception.impl.AuthenticationException;
import ml.socshared.service.auth.service.OAuthService;
import ml.socshared.service.auth.service.SessionService;
import ml.socshared.service.auth.service.UserService;
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

    private final AuthenticationUserService authenticationUserService;
    private final UserService userService;
    private final SessionService sessionService;

    public OAuth2TokenResponse createTokenByUsernameAndPassword(User user, Client client) {
        Claims claimsAccess = JwtClaimsBuilder.buildJwtClaimsByUsernameAndPassword(user, client);
        String accessToken = generationToken(claimsAccess, validityAccessTokenInMilliseconds);
        Claims claimsRefresh = Jwts.claims().setSubject(user.getUserId().toString());
        String refreshToken = generationToken(claimsRefresh, validityRefreshTokenInMilliseconds);

        String sessionId = claimsAccess.get("session_state", String.class);

        Session session = createSession(client, user, UUID.fromString(sessionId));
        OAuth2TokenResponse oAuth2TokenResponse = OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .expireIn(claimsAccess.getExpiration().getTime() + "")
                .refreshToken(refreshToken)
                .sessionId(session.getSessionId().toString())
                .tokenType("bearer")
                .build();

        OAuth2AccessToken aToken = new OAuth2AccessToken();
        aToken.setAccessToken(accessToken);
        aToken.setExpireIn(claimsAccess.getExpiration().getTime() + "");
        aToken.setSession(session);
        aToken.setTokenType("bearer");
        session.setAccessToken(aToken);
        session.setOfflineSession(false);

        OAuth2RefreshToken rToken = new OAuth2RefreshToken();
        rToken.setRefreshExpiresIn(claimsRefresh.getExpiration().getTime() + "");
        rToken.setRefreshToken(refreshToken);
        session.setRefreshToken(rToken);
        session.setActiveSession(true);

        sessionService.save(session);

        return oAuth2TokenResponse;
    }

    public OAuth2TokenResponse createTokenByRefreshToken(String refreshToken, Client client) {
        Jws<Claims> refreshClaims = getJwsClaimsFromToken(refreshToken);
        UUID userId = UUID.fromString(refreshClaims.getBody().getSubject());
        Session session = sessionService.findByClientIdAndUserId(client.getClientId(), userId);
        if (session == null)
            throw new AuthenticationException("Invalid session");

        Jws<Claims> accessClaims = getJwsClaimsFromToken(session.getAccessToken().getAccessToken());

        String accessToken = generationToken(accessClaims.getBody(), validityAccessTokenInMilliseconds);
        Claims claimsRefresh = refreshClaims.getBody();
        String newRefreshToken = generationToken(claimsRefresh, validityRefreshTokenInMilliseconds);

        OAuth2TokenResponse response = OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .expireIn(accessClaims.getBody().getExpiration().getTime()+"")
                .refreshToken(newRefreshToken)
                .sessionId(session.getSessionId().toString())
                .tokenType("bearer")
                .build();

        OAuth2AccessToken aToken = new OAuth2AccessToken();
        aToken.setAccessToken(accessToken);
        aToken.setExpireIn(accessClaims.getBody().getExpiration().getTime() + "");
        aToken.setSession(session);
        aToken.setTokenType("bearer");
        session.setAccessToken(aToken);
        session.setOfflineSession(false);

        OAuth2RefreshToken rToken = new OAuth2RefreshToken();
        rToken.setRefreshExpiresIn(claimsRefresh.getExpiration().getTime() + "");
        rToken.setRefreshToken(refreshToken);
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

        return SpringUserDetails.builder()
                .authorities(roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList()))
                .username(claims.get("username", String.class))
                .firstName(claims.get("firstname", String.class))
                .lastName(claims.get("lastname", String.class))
                .email(claims.get("email", String.class))
                .accountNonLocked(claims.get("account_non_locked", Boolean.class))
                .build();
    }

    public ServiceTokenResponse buildServiceToken(ServiceTokenRequest request) {
        Claims claimsAccess = Jwts.claims().setSubject(request.getFromServiceId().toString());
        claimsAccess.put("auth_time", new Date().getTime());
        claimsAccess.put("typ", "bearer");
        claimsAccess.put("from_service", request.getFromServiceId().toString());
        claimsAccess.put("to_service", request.getToServiceId().toString());

        if (request.getUsersAccessToken() != null) {
            Claims claimsFromUserToken = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(request.getUsersAccessToken()).getBody();
            String userId = claimsFromUserToken.getSubject();
            claimsAccess.put("user_id", userId);
            String sessionId = claimsFromUserToken.get("session_state", String.class);
            claimsAccess.put("session_state", sessionId);
            ArrayList<String> roles = claimsFromUserToken.get("roles", ArrayList.class);
            roles.add("SERVICE");
            claimsAccess.put("roles", roles);
            String username = claimsFromUserToken.get("username", String.class);
            claimsAccess.put("username", username);
            String firstname = claimsFromUserToken.get("firstname", String.class);
            claimsAccess.put("firstname", firstname);
            String lastname = claimsFromUserToken.get("lastname", String.class);
            claimsAccess.put("lastname", lastname);
            String email = claimsFromUserToken.get("email", String.class);
            claimsAccess.put("email", email);
            Boolean emailVerified = claimsFromUserToken.get("email_verified", Boolean.class);
            claimsAccess.put("email_verified", emailVerified);
            Boolean accountNonLocked = claimsFromUserToken.get("account_non_locked", Boolean.class);
            claimsAccess.put("account_non_locked", accountNonLocked);
            Boolean isResetPassword = claimsFromUserToken.get("is_reset_password", Boolean.class);
            claimsAccess.put("is_reset_password", isResetPassword);
        }

        Date now = new Date();
        Date expireIn = new Date(now.getTime() + 1000 * 60 * 30);
        JwtBuilder builder = Jwts.builder()
                .setClaims(claimsAccess)
                .setIssuedAt(now)
                .setExpiration(expireIn)
                .signWith(SignatureAlgorithm.HS512, secretKey);


        return ServiceTokenResponse.builder()
                .expireIn(expireIn.getTime() + "")
                .fromService(request.getFromServiceId().toString())
                .toService(request.getToServiceId().toString())
                .token(builder.compact())
                .build();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = getJwsClaimsFromToken(token);
            UUID userId = UUID.fromString(claims.getBody().getSubject());
            UUID clientId = UUID.fromString(claims.getBody().get("client_id", String.class));
            Date date = claims.getBody().getExpiration();
            if (date.before(new Date())) {
                log.warn("JWT Token is expired.");
                Session session = sessionService.findByClientIdAndUserId(userId, clientId);
                session.setActiveSession(false);
                sessionService.save(session);
                return false;
            }
            return true;
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
            if (date.before(new Date())) {
                log.warn("JWT Token is expired.");
                Session session = sessionService.findByClientIdAndUserId(userId, clientId);
                session.setActiveSession(false);
                session.setOfflineSession(true);
                sessionService.save(session);
                return false;
            }
            return true;
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

    public Session createSession(Client client, User user, UUID sessionId) {
        Session session = sessionService.findByClientIdAndUserId(client.getClientId(), user.getUserId());
        if (session == null) {
            session = new Session();
            session.setClient(client);
            session.setUser(user);
            session.setActiveSession(false);
            session.setOfflineSession(true);
            session.setSessionId(sessionId);
        }
        session.setActiveSession(true);
        return session;
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            return token.substring(6).trim();
        }
        return null;
    }
}
