package ml.socshared.auth.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@ToString
@Setter
@Getter
@EqualsAndHashCode
public class OAuth2TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expire_in")
    private Long expireIn;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("session_id")
    private String sessionId;

}
