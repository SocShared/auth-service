package ml.socshared.service.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.service.auth.domain.request.NewUserRequest;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1")
@Validated
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService service;

    @PostMapping(value = "/public/users", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse add(@Valid @RequestBody NewUserRequest request) {
        return service.add(request);
    }

}
