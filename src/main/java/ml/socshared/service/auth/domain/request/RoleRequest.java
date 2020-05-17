package ml.socshared.service.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class RoleRequest {

    @NotEmpty
    private String name;
}
