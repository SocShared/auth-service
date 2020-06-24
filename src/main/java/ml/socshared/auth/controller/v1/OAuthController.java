package ml.socshared.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.request.CheckTokenRequest;
import ml.socshared.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.service.OAuthService;
import ml.socshared.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.auth.exception.impl.OAuth2Exception;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public OAuth2TokenResponse getTokenByForm(@RequestParam Map<String, String> oAuthFlowRequest) {
        return getToken(OAuthFlowRequest.fromMap(oAuthFlowRequest));
    }

    @PostMapping(value = "/oauth/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public OAuth2TokenResponse getToken(@Valid @RequestBody OAuthFlowRequest oAuthFlowRequest) {

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

    @PostMapping(value = "/oauth/validate_token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
                consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SuccessResponse isValidateToken(@Valid @RequestBody CheckTokenRequest request) {
        return oAuthService.checkValidateAccessToken(request);
    }

}
