package ml.socshared.auth.domain.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.util.password.ValidPassword;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UpdatePasswordRequest {

    @ValidPassword
    private String password;

}
