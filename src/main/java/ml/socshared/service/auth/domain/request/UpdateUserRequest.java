package ml.socshared.service.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.service.auth.util.password.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class UpdateUserRequest {
    @NotEmpty
    @Email
    private String email;
    private String firstname;
    private String lastname;

}
