package ml.socshared.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.util.password.ValidPassword;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
public class NewUserRequest {

    @NotNull(message = "Username не может быть пустым")
    @Pattern(regexp = "[a-zA-Z0-9+=_\\s]{3,60}", message = "Username должен состоять только из букв, цифр и нижнего подчеркивания")
    private String username;
    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Неверно задан email")
    private String email;

    private String firstname;
    private String lastname;

    @ValidPassword
    private String password;

}
