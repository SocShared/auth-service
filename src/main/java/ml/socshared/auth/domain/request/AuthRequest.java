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

    private String username;
    private String password;

}
