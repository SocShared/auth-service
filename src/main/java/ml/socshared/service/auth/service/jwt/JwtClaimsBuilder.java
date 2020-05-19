package ml.socshared.service.auth.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import ml.socshared.service.auth.domain.request.ServiceTokenRequest;
import ml.socshared.service.auth.domain.response.ServiceTokenResponse;
import ml.socshared.service.auth.entity.Client;
import ml.socshared.service.auth.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.UUID;

public class JwtClaimsBuilder {

    public static Claims buildJwtClaimsByUsernameAndPassword(User user, Client client) {
        Claims claimsAccess = Jwts.claims().setSubject(user.getUserId().toString());
        claimsAccess.put("auth_time", new Date().getTime());
        claimsAccess.put("session_state", UUID.randomUUID().toString());
        claimsAccess.put("typ", "bearer");
        claimsAccess.put("roles", user.getRoleNames());
        claimsAccess.put("client_id", client.getClientId());
        claimsAccess.put("client_name", client.getName());
        claimsAccess.put("username", user.getUsername());
        claimsAccess.put("firstname", user.getFirstname());
        claimsAccess.put("lastname", user.getLastname());
        claimsAccess.put("email", user.getEmail());
        claimsAccess.put("email_verified", user.getEmailVerified());
        claimsAccess.put("name", user.getFirstname() + " " + user.getLastname());
        claimsAccess.put("account_non_locked", user.getAccountNonLocked());
        claimsAccess.put("is_reset_password", user.getResetPassword());

        return claimsAccess;
    }

}
