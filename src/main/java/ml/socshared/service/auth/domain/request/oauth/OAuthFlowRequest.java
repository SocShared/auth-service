package ml.socshared.service.auth.domain.request.oauth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class OAuthFlowRequest {

    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String refreshToken;

    @NotNull
    private TypeFlow grantType;

}
