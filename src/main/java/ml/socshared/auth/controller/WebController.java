package ml.socshared.auth.controller;

import ml.socshared.auth.domain.model.SpringUserDetails;
import ml.socshared.auth.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebController {

    @GetMapping(value = "/sign_in")
    public ModelAndView signIn(Model model, HttpServletRequest request) {
        request.getSession().setAttribute("resultTest", null);
        return new ModelAndView("sign_in");
    }

    /*@PostMapping(value = "/sign_in")
    public String signIn(@ModelAttribute @Valid User user) {
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setName("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);
        user.setStatus(Status.ACTIVE);
        User u = authService.create(user);
        return "redirect:/";
    }

    @GetMapping(value = "/exit")
    public String exit(Model model,
                       @CookieValue(value = "ut", required = false) Cookie ut,
                       @CookieValue(value = "rt", required = false) Cookie rt,
                       HttpServletRequest request, HttpServletResponse response) {
        request.getSession().setAttribute("resultTest", null);
        model.addAttribute("user", new User());
        request.getSession().invalidate();
        if (ut != null) {
            ut.setValue(null);
            ut.setPath("/");
            response.addCookie(ut);
        }
        if (rt != null) {
            rt.setValue(null);
            rt.setPath("/");
            response.addCookie(rt);
        }
        return "redirect:/";
    }*/

}
