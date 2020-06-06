package ml.socshared.auth.controller.web;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.response.ClientResponse;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.entity.AuthorizationCode;
import ml.socshared.auth.service.ClientService;
import ml.socshared.auth.service.OAuthService;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Controller
@RequestMapping
@Validated
@RequiredArgsConstructor
public class OAuthWebController {

    private final OAuthService oAuthService;
    private final UserService userService;
    private final ClientService clientService;
    private final JwtTokenProvider provider;

    @GetMapping("/oauth/authorize")
    public String authorizedCode(@NotNull @RequestParam(name = "client_id") UUID clientId,
                                 @NotNull @RequestParam(name = "response_type") String responseType,
                                 @NotNull @RequestParam(name = "state") String state,
                                 @NotNull @RequestParam(name = "redirect_uri") String redirectUri,
                                 @CookieValue(name = "JWT", defaultValue = "") String accessToken,
                                 Model model) {
        if (responseType.equals("code")) {
            if (!accessToken.isEmpty() && provider.validateAccessToken(accessToken)) {
                AuthorizationCode code = oAuthService.getAuthorizationCode(
                        provider.getUserIdByToken(accessToken),
                        clientId,
                        redirectUri
                );
                UserResponse userResponse = userService.findById(code.getUserId());
                ClientResponse clientResponse = clientService.findById(code.getClientId());
                model.addAttribute("user", userResponse);
                model.addAttribute("client", clientResponse);
                model.addAttribute("redirect_uri", code.getRedirectUri());
                model.addAttribute("code", code.getGeneratingLink());
                model.addAttribute("state", state);
                return "proof_rights";
            } else {
                return "redirect:/signin";
            }
        }
        return "redirect:https://socshared.ml";
    }


}
