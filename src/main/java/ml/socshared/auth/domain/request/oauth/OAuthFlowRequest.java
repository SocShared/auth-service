package ml.socshared.auth.domain.request.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("code")
    private String code;
    @JsonProperty("redirect_uri")
    private String redirectUri;

    @NotNull
    @JsonProperty("grant_type")
    private TypeFlow grantType;

}
