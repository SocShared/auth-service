package ml.socshared.service.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@ToString
public class ScopeRequest {

    @NotEmpty
    private String name;

}
