package ml.socshared.service.auth.domain.response;

import lombok.*;

@Builder
@ToString
@Setter
@Getter
@EqualsAndHashCode
public class OAuth2TokenResponse {

    private String accessToken;
    private String expireIn;
    private String tokenType;
    private String refreshToken;
    private String sessionId;

}
