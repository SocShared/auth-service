package ml.socshared.auth.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import ml.socshared.auth.entity.Client;
import ml.socshared.auth.entity.User;

import java.util.Date;
import java.util.UUID;

public class JwtClaimsBuilder {

    public static Claims buildJwtClaimsByUsernameAndPassword(User user, Client client) {
        Claims claimsAccess = Jwts.claims().setSubject(user.getUserId().toString());
        claimsAccess.put("auth_time", new Date().getTime());
        claimsAccess.put("session_state", UUID.randomUUID().toString());
        claimsAccess.put("typ", "bearer");
        claimsAccess.put("roles", user.getRoleNames());
        claimsAccess.put("client_id", client.getClientId().toString());
        claimsAccess.put("client_name", client.getName());
        claimsAccess.put("username", user.getUsername());
        claimsAccess.put("firstname", user.getFirstname());
        claimsAccess.put("lastname", user.getLastname());
        claimsAccess.put("email", user.getEmail());
        claimsAccess.put("email_verified", user.getEmailVerified());
        claimsAccess.put("account_non_locked", user.getAccountNonLocked());
        claimsAccess.put("is_reset_password", user.getResetPassword());

        return claimsAccess;
    }

}
