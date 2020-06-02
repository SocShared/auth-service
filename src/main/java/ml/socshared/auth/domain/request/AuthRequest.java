package ml.socshared.auth.domain.request;

import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.util.password.ValidPassword;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AuthRequest {

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9+=_\\s]{3,60}", message = "Username должен состоять только из букв, цифр и нижнего подчеркивания")
    private String username;
    @NotNull
    @ValidPassword
    private String password;

}
