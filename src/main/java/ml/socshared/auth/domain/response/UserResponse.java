package ml.socshared.auth.domain.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import ml.socshared.auth.config.CustomLocalDateTimeSerializer;
import ml.socshared.auth.entity.Role;
import ml.socshared.auth.entity.User;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID userId;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime timeOnline;
    private Set<Role> roles;

    public UserResponse() {}

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.timeOnline = user.getTimeOnline();
        this.roles = user.getRoles();
    }
}
