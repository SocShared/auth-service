package ml.socshared.service.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.util.password.ValidPassword;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
public class NewUserRequest {

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9+=\\s]{3,60}")
    private String username;
    @NotEmpty
    @Email
    private String email;

    private String firstname;
    private String lastname;

    @ValidPassword
    private String password;

}
