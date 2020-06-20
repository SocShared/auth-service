package ml.socshared.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.model.UserModel;
import ml.socshared.auth.domain.request.UpdateUserRequest;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import ml.socshared.auth.domain.request.UpdatePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService service;
    private final JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users")
    public Page<UserModel> findAll(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                   @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return service.findAll(page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/{userId}")
    public UserResponse findById(@PathVariable UUID userId) {
        return service.findById(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/username/{username}")
    public UserResponse findByUsername(@PathVariable String username) {
        return service.findByUsername(username);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/email/{email}")
    public UserResponse findByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/activation")
    public UserResponse activation(@PathVariable UUID userId) {
        return service.activation(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/deactivation")
    public UserResponse deactivation(@PathVariable UUID userId) {
        return service.deactivation(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/roles/{roleId}/add")
    public UserResponse addRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return service.addRole(userId, roleId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/roles/{roleId}/delete")
    public UserResponse removeRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return service.removeRole(userId, roleId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @DeleteMapping(value = "/private/users/{userId}")
    public void delete(@PathVariable UUID userId) {
        service.deleteById(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}")
    public UserResponse updateData(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        return service.update(userId, request);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/password")
    public UserResponse updatePassword(@PathVariable UUID userId, @Valid @RequestBody UpdatePasswordRequest request) {
        return service.updatePassword(userId, request);
    }

}
