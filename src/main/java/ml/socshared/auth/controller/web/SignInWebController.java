package ml.socshared.auth.controller.web;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.client.MailSenderClient;
import ml.socshared.auth.domain.request.*;
import ml.socshared.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.auth.domain.request.oauth.TypeFlow;
import ml.socshared.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.entity.GeneratingCode;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.exception.impl.AuthenticationException;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.service.OAuthService;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class SignInWebController {

    private final OAuthService oAuthService;
    private final JwtTokenProvider provider;

    @ModelAttribute("user")
    public AuthRequest getAuthRequest() {
        return new AuthRequest();
    }

    @GetMapping("/signin")
    public String showSignInForm(@RequestParam(name = "client_id", required = false) UUID clientId, @RequestParam(name = "response_type", required = false) String responseType,
                                 @RequestParam(name = "state", required = false) String state, @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                                 @CookieValue(value = "JWT_AT", defaultValue = "") String jwtToken, @CookieValue(value = "JWT_RT", defaultValue = "") String rtToken,
                                 HttpServletResponse response) {
        if (jwtToken.isEmpty())
            return "signin";
        else if (!provider.validateAccessToken(jwtToken)) {
            if (!provider.validateRefreshToken(rtToken))
                return "signin";
            else {
                OAuthFlowRequest req = new OAuthFlowRequest();
                req.setClientId("360dad92-ecb1-44e7-990a-3152d2642919");
                req.setClientSecret("cb456410-85ca-43b5-9a12-87171ad84516");
                req.setGrantType(TypeFlow.REFRESH_TOKEN);
                req.setRefreshToken(rtToken);
                OAuth2TokenResponse res = oAuthService.getTokenByRefreshToken(req);
                Cookie accessToken = new Cookie("JWT_AT", res.getAccessToken());
                accessToken.setMaxAge(24 * 60 * 60);
                accessToken.setSecure(true);
                accessToken.setHttpOnly(true);
                accessToken.setPath("/");
                accessToken.setDomain("socshared.ml");
                response.addCookie(accessToken);

                Cookie refreshToken = new Cookie("JWT_RT", res.getRefreshToken());
                refreshToken.setMaxAge(24 * 60 * 60 * 30);
                refreshToken.setSecure(true);
                refreshToken.setHttpOnly(true);
                refreshToken.setPath("/");
                refreshToken.setDomain("socshared.ml");
                response.addCookie(refreshToken);
                if (clientId != null && responseType != null && state != null && redirectUri != null)
                    return "redirect:"+String.format("/oauth/authorize?client_id=%s&response_type=%s&state=%s&redirect_uri=%s", clientId, responseType, state, redirectUri);
            }
        }
        return "redirect:https://socshared.ml/social";
    }

    @PostMapping("/signin")
    public String signIn(@Valid @ModelAttribute("user") AuthRequest request, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors())
            return "signin";
        OAuthFlowRequest req = new OAuthFlowRequest();
        req.setClientId("360dad92-ecb1-44e7-990a-3152d2642919");
        req.setClientSecret("cb456410-85ca-43b5-9a12-87171ad84516");
        req.setGrantType(TypeFlow.PASSWORD);
        req.setUsername(request.getUsername());
        req.setPassword(request.getPassword());

        try {
            OAuth2TokenResponse token = oAuthService.getTokenByUsernameAndPassword(req);
            Cookie accessToken = new Cookie("JWT_AT", token.getAccessToken());
            accessToken.setMaxAge(24 * 60 * 60);
            accessToken.setSecure(true);
            accessToken.setHttpOnly(true);
            accessToken.setPath("/");
            accessToken.setDomain("socshared.ml");
            response.addCookie(accessToken);

            Cookie refreshToken = new Cookie("JWT_RT", token.getRefreshToken());
            refreshToken.setMaxAge(24 * 60 * 60 * 30);
            refreshToken.setSecure(true);
            refreshToken.setHttpOnly(true);
            refreshToken.setPath("/");
            refreshToken.setDomain("socshared.ml");
            response.addCookie(refreshToken);
        } catch (AuthenticationException exc) {
            bindingResult.addError(new FieldError("user", "password", "Неверный логин или пароль"));
            if (bindingResult.hasErrors()) {
                return "signin";
            }
        }
        return "redirect:https://socshared.ml/social";
    }

}
