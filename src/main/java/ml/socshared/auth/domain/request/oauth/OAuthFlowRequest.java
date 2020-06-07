package ml.socshared.auth.domain.request.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Map;

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

    public static OAuthFlowRequest fromMap(Map<String, String> map) {
        OAuthFlowRequest request = new OAuthFlowRequest();
        request.setClientId(map.get("client_id"));
        request.setClientSecret(map.get("client_secret"));
        request.setRefreshToken(map.get("refresh_token"));
        request.setUsername(map.get("username"));
        request.setPassword(map.get("password"));
        request.setGrantType(TypeFlow.valueOf(map.get("grant_type").toUpperCase()));
        request.setCode(map.get("code"));
        request.setRedirectUri(map.get("redirect_uri"));

        return request;
    }
}
