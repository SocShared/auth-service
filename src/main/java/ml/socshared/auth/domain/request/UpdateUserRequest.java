package ml.socshared.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class UpdateUserRequest {

    private String firstname;
    private String lastname;

}
