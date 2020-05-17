package ml.socshared.service.auth.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import ml.socshared.service.auth.entity.Client;
import ml.socshared.service.auth.entity.User;

import java.util.Date;
import java.util.UUID;

public class JwtClaimsBuilder {

    public static Claims buildJwtClaimsByUsernameAndPassword(User user, Client client) {
        Claims claimsAccess = Jwts.claims().setSubject(user.getUserId().toString());
        claimsAccess.put("auth_time", new Date().getTime());
        claimsAccess.put("session_state", UUID.randomUUID());
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
        claimsAccess.put("account_non_expired", user.getAccountNonExpired());
        claimsAccess.put("account_non_locked", user.getAccountNonLocked());
        claimsAccess.put("credentials_non_expired", user.getCredentialsNonExpired());
        claimsAccess.put("last_password_reset_date", user.getLastPasswordResetDate().getTime());

        return claimsAccess;
    }

}
