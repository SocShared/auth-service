package ml.socshared.auth.controller.web;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.request.UpdatePasswordRequest;
import ml.socshared.auth.entity.GeneratingCode;
import ml.socshared.auth.service.OAuthService;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class GeneratingLinkController {

    private final UserService userService;

    @ModelAttribute("password")
    public UpdatePasswordRequest getUpdatePasswordRequest() {
        return new UpdatePasswordRequest();
    }

    @GetMapping(value = "/account/{generatingLink}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String processGenerationLink(@PathVariable String generatingLink, Model model) {
        GeneratingCode generatingCode = userService.processGenerationLink(generatingLink);
        if (generatingCode != null) {
            switch (generatingCode.getType()) {
                case RESET_PASSWORD:
                    UpdatePasswordRequest request = getUpdatePasswordRequest();
                    request.setUserId(generatingCode.getUserId());
                    model.addAttribute("password", request);
                    return "set_password";
                case EMAIL_CONFIRMATION:
                    model.addAttribute("title", "Подтверждение электронной почты");
                    model.addAttribute("text", " Вы успешно подвтердили свою электронную почту.");
                    return "success";
            }
        }
        return "redirect:https://socshared.ml";
    }

}
