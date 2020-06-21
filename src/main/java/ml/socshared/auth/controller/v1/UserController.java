package ml.socshared.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.model.UserModel;
import ml.socshared.auth.domain.request.UpdateUserRequest;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.domain.response.stat.ActiveUsersResponse;
import ml.socshared.auth.domain.response.stat.AllUsersResponse;
import ml.socshared.auth.domain.response.stat.NewUsersResponse;
import ml.socshared.auth.domain.response.stat.OnlineUsersResponse;
import ml.socshared.auth.service.SessionService;
import ml.socshared.auth.service.UserService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import ml.socshared.auth.domain.request.UpdatePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;
    private final JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users")
    public Page<UserModel> findAll(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                   @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return userService.findAll(page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/{userId}")
    public UserResponse findById(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/username/{username}")
    public UserResponse findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/email/{email}")
    public UserResponse findByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/activation")
    public UserResponse activation(@PathVariable UUID userId) {
        return userService.activation(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/deactivation")
    public UserResponse deactivation(@PathVariable UUID userId) {
        return userService.deactivation(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/roles/{roleId}/add")
    public UserResponse addRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return userService.addRole(userId, roleId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/roles/{roleId}/delete")
    public UserResponse removeRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return userService.removeRole(userId, roleId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @DeleteMapping(value = "/private/users/{userId}")
    public void delete(@PathVariable UUID userId) {
        userService.deleteById(userId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}")
    public UserResponse updateData(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(userId, request);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/password")
    public UserResponse updatePassword(@PathVariable UUID userId, @Valid @RequestBody UpdatePasswordRequest request) {
        return userService.updatePassword(userId, request);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/online/count")
    public OnlineUsersResponse getOnlineUsersCount() {
        return userService.onlineUsers();
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/active/count")
    public ActiveUsersResponse getActiveUsersCount() {
        return sessionService.activeUsers();
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/new/count")
    public NewUsersResponse getNewUsersCount() {
        return userService.newUsers();
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/all/count")
    public AllUsersResponse getAllUsersCount() {
        return userService.allUsers();
    }

}
