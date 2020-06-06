package ml.socshared.auth.domain.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.util.password.ValidPassword;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UpdatePasswordRequest {

    @NotNull
    private UUID userId;
    @ValidPassword
    private String password;
    @ValidPassword
    private String repeatPassword;

}
