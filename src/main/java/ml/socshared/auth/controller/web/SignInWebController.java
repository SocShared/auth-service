package ml.socshared.auth.controller.web;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.client.MailSenderClient;
import ml.socshared.auth.domain.request.AuthRequest;
import ml.socshared.auth.domain.request.NewUserRequest;
import ml.socshared.auth.domain.request.SendMessageMailConfirmRequest;
import ml.socshared.auth.domain.request.UpdatePasswordRequest;
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

@Controller
@RequestMapping
@RequiredArgsConstructor
public class SignInWebController {

    private final OAuthService oAuthService;
    private final UserService userService;
    private final JwtTokenProvider provider;

    @ModelAttribute("user")
    public AuthRequest getAuthRequest() {
        return new AuthRequest();
    }

    @ModelAttribute("password")
    public UpdatePasswordRequest getUpdatePasswordRequest() {
        return new UpdatePasswordRequest();
    }

    @GetMapping("/signin")
    public String showSignInForm(@CookieValue(value = "JWT_AT", defaultValue = "") String jwtToken, @CookieValue(value = "JWT_RT", defaultValue = "") String rtToken,
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
            }
        }
        return "redirect:https://socshared.ml/social";
    }

    @PostMapping("/signin")
    public String signInPost(@Valid @ModelAttribute("user") AuthRequest request, BindingResult bindingResult, HttpServletResponse response) {
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

    @GetMapping("/resetpass")
    public String showResetPassForm(@CookieValue(value = "JWT_AT", defaultValue = "") String jwtToken, @CookieValue(value = "JWT_RT", defaultValue = "") String rtToken,
                                    HttpServletResponse response) {
        if (jwtToken.isEmpty())
            return "reset_password";
        else if (!provider.validateAccessToken(jwtToken)) {
            if (!provider.validateRefreshToken(rtToken))
                return "reset_password";
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
            }
        }
        return "redirect:https://socshared.ml/social";
    }

    @PostMapping("/resetpass")
    public String resetPasswordPost(@Valid @ModelAttribute("email") String email, Model model, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors())
            return "reset_password";

        try {
            userService.resetPassword(email);
        } catch (HttpNotFoundException exc) {
            bindingResult.addError(new ObjectError("email", "Введенный email не найден."));
            if (bindingResult.hasErrors()) {
                return "reset_password";
            }
        }

        model.addAttribute("text", " Вам вышло письмо на почту для сброса пароля.\n" +
                "                <a href=\"https://socshared.ml\">Вернуться на главную</a>.");

        return "success";
    }

    @PostMapping("/setpass")
    public String setPass(@Valid @ModelAttribute("password") UpdatePasswordRequest request, Model model,
                          BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors())
            return "reset_password";

        if (!request.getPassword().equals(request.getRepeatPassword())) {
            bindingResult.addError(new ObjectError("email", "Введенный email не найден."));
            if (bindingResult.hasErrors()) {
                return "reset_password";
            }
        }

        userService.updatePassword(request.getUserId(), request);

        model.addAttribute("text", " Вы успешно изменили пароль.\n" +
                "                <a href=\"https://socshared.ml\">Вернуться на главную</a>.");

        return "success";
    }

    @GetMapping(value = "/account/{generatingLink}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String processGenerationLink(@PathVariable String generatingLink, Model model) {
        GeneratingCode generatingCode = userService.processGenerationLink(generatingLink);
        if (generatingCode != null) {
            switch (generatingCode.getType()) {
                case RESET_PASSWORD:
                    getUpdatePasswordRequest().setUserId(generatingCode.getUserId());
                    return "set_password";
                case EMAIL_CONFIRMATION:
                    model.addAttribute("text", " Вы успешно подвтердили свою электронную почту.\n" +
                            "                <a href=\"https://socshared.ml\">Вернуться на главную</a>.");
                    return "success";
            }
        }
        return "redirect:/";
    }
}
