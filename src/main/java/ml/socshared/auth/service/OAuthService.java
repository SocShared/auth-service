package ml.socshared.auth.service;

import ml.socshared.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.auth.domain.response.OAuth2TokenResponse;

public interface OAuthService {

    OAuth2TokenResponse getTokenByUsernameAndPassword(OAuthFlowRequest request);
    OAuth2TokenResponse getTokenByRefreshToken(OAuthFlowRequest request);
    OAuth2TokenResponse getTokenByAuthorizationCode(OAuthFlowRequest request);
    OAuth2TokenResponse getTokenByClientCredentials(OAuthFlowRequest request);

}
