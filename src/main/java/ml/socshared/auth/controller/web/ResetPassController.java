package ml.socshared.auth.controller.web;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.request.SendMailRequest;
import ml.socshared.auth.domain.request.UpdatePasswordRequest;
import ml.socshared.auth.domain.request.oauth.OAuthFlowRequest;
import ml.socshared.auth.domain.request.oauth.TypeFlow;
import ml.socshared.auth.domain.response.OAuth2TokenResponse;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.service.OAuthService;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ResetPassController {

    private final OAuthService oAuthService;
    private final UserService userService;
    private final JwtTokenProvider provider;

    @ModelAttribute("password")
    public UpdatePasswordRequest getUpdatePasswordRequest() {
        return new UpdatePasswordRequest();
    }

    @ModelAttribute("email")
    public SendMailRequest getSendMailRequest() {
        return new SendMailRequest();
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
    public String resetPasswordPost(@Valid @ModelAttribute("email") SendMailRequest request, Model model, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors())
            return "reset_password";

        try {
            userService.resetPassword(request.getEmail());
        } catch (HttpNotFoundException exc) {
            bindingResult.addError(new FieldError("email", "email", "Введенный email не найден."));
            if (bindingResult.hasErrors()) {
                return "reset_password";
            }
        }

        model.addAttribute("title", "Изменение пароля");
        model.addAttribute("text", " Вам вышло письмо на почту для сброса пароля.");

        return "success";
    }

    @PostMapping("/setpass/{userId}")
    public String setPass(@Valid @ModelAttribute("password") UpdatePasswordRequest request, @PathVariable UUID userId, Model model,
                          BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors())
            return "set_password";

        if (!request.getPassword().equals(request.getRepeatPassword())) {
            bindingResult.addError(new ObjectError("email", "Введенный email не найден."));
            if (bindingResult.hasErrors()) {
                return "set_password";
            }
        }

        userService.updatePassword(userId, request);

        model.addAttribute("title", "Изменение пароля");
        model.addAttribute("text", " Вы успешно изменили пароль.");

        return "success";
    }

}
