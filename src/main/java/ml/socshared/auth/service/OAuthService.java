package ml.socshared.auth.service;

import ml.socshared.auth.domain.request.CheckTokenRequest;
import ml.socshared.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.entity.AuthorizationCode;

import java.util.UUID;

public interface OAuthService {

    OAuth2TokenResponse getTokenByUsernameAndPassword(OAuthFlowRequest request);
    OAuth2TokenResponse getTokenByRefreshToken(OAuthFlowRequest request);
    OAuth2TokenResponse getTokenByAuthorizationCode(OAuthFlowRequest request);
    OAuth2TokenResponse getTokenByClientCredentials(OAuthFlowRequest request);
    AuthorizationCode getAuthorizationCode(UUID userId, UUID clientId, String redirectUri);
    SuccessResponse checkValidateAccessToken(CheckTokenRequest request);

}
