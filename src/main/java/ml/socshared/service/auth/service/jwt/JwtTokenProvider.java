package ml.socshared.service.auth.service.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.model.SpringUserDetails;
import ml.socshared.service.auth.domain.response.OAuth2TokenResponse;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

        Session session = createSession(client, user);
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
        String[] roles = claims.get("roles", String[].class);

        return SpringUserDetails.builder()
                .authorities(Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .username(claims.get("username", String.class))
                .firstName(claims.get("firstname", String.class))
                .lastName(claims.get("lastname", String.class))
                .email(claims.get("email", String.class))
                .accountNonExpired(claims.get("account_non_expired", Boolean.class))
                .accountNonLocked(claims.get("account_non_locked", Boolean.class))
                .credentialsNonExpired(claims.get("credentials_non_expired", Boolean.class))
                .lastPasswordResetDate(new Date(claims.get("last_password_reset_date", Long.class)))
                .build();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = getJwsClaimsFromToken(token);
            UUID userId = UUID.fromString(claims.getBody().get("user_id", String.class));
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
            UUID userId = UUID.fromString(claims.getBody().get("user_id", String.class));
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
        return UUID.fromString(claims.getBody().get("user_id", String.class));
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
            session.setClient(client);
            session.setUser(user);
            session.setActiveSession(false);
            session.setOfflineSession(true);
            session.setSessionId(UUID.randomUUID());
        }
        session.setActiveSession(true);
        return session;
    }
}
