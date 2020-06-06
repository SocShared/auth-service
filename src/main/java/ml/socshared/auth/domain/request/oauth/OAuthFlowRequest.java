package ml.socshared.auth.domain.request.oauth;

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
    private String code;
    private String redirectUri;

    @NotNull
    private TypeFlow grantType;

}
