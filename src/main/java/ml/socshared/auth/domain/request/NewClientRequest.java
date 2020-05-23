package ml.socshared.auth.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.socshared.auth.entity.Client;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class NewClientRequest {

    private String name;

    @NotNull
    private Client.AccessType accessType;

    private String validRedirectUri;

}
