package ml.socshared.service.auth.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ml.socshared.service.auth.entity.User;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID userId;
    private String username;
    private String email;
    private String firstname;
    private String lastname;

    public UserResponse() {}

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
    }
}
