package ml.socshared.service.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.service.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.service.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.service.auth.exception.impl.OAuth2Exception;
import ml.socshared.service.auth.service.OAuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public OAuth2TokenResponse getToken(@RequestBody OAuthFlowRequest oAuthFlowRequest) {
        switch (oAuthFlowRequest.getGrantType()) {
            case PASSWORD:
                return oAuthService.getTokenByUsernameAndPassword(oAuthFlowRequest);
            case REFRESH_TOKEN:
                return oAuthService.getTokenByRefreshToken(oAuthFlowRequest);
            case AUTHORIZATION_CODE:
                return oAuthService.getTokenByAuthorizationCode(oAuthFlowRequest);
            case CLIENT_CREDENTIALS:
                return oAuthService.getTokenByClientCredentials(oAuthFlowRequest);
        }
        throw new OAuth2Exception("GrantType undefined.");
    }

}
