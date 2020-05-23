package ml.socshared.auth.domain.response;

import lombok.*;

@Builder
@ToString
@Setter
@Getter
@EqualsAndHashCode
public class OAuth2TokenResponse {

    private String accessToken;
    private Long expireIn;
    private String tokenType;
    private String refreshToken;
    private String sessionId;

}
