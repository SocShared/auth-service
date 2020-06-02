package ml.socshared.auth.controller.web;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.request.NewUserRequest;
import ml.socshared.auth.exception.impl.EmailIsExistsException;
import ml.socshared.auth.exception.impl.UsernameAndEmailIsExistsException;
import ml.socshared.auth.exception.impl.UsernameIsExistsException;
import ml.socshared.auth.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class SignUpWebController {

    @ModelAttribute("user")
    public NewUserRequest userRequest() {
        return new NewUserRequest();
    }

    private final UserService service;

    @GetMapping("/signup")
    public String showForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String submitForm(@Valid @ModelAttribute("user") NewUserRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "signup";
        try {
            service.add(request);
        } catch (UsernameIsExistsException exc) {
            bindingResult.addError(new FieldError("user", "username", "Username уже существует."));
            exc.printStackTrace();
        } catch (EmailIsExistsException exc) {
            bindingResult.addError(new FieldError("user", "email", "Email уже существует."));
            exc.printStackTrace();
        } catch (UsernameAndEmailIsExistsException exc) {
            bindingResult.addError(new FieldError("user", "username", "Username уже существует."));
            bindingResult.addError(new FieldError("user", "email", "Email уже существует."));
            exc.printStackTrace();
        }
        if (bindingResult.hasErrors())
            return "signup";
        return "success";
    }

}
