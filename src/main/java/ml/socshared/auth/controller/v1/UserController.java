package ml.socshared.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.request.UpdateUserRequest;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.domain.response.UserResponse;
import ml.socshared.auth.domain.response.stat.ActiveUsersResponse;
import ml.socshared.auth.domain.response.stat.AllUsersResponse;
import ml.socshared.auth.domain.response.stat.NewUsersResponse;
import ml.socshared.auth.domain.response.stat.OnlineUsersResponse;
import ml.socshared.auth.entity.User;
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
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

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
    @PutMapping(value = "/private/users/{userId}")
    public void updateData(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        userService.update(userId, request);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PutMapping(value = "/private/users/{userId}/password")
    public void updatePassword(@PathVariable UUID userId, @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(userId, request);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/online/count")
    public OnlineUsersResponse getOnlineUsersCount() {
        return userService.onlineUsersCount();
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/online")
    public Page<User> getOnlineUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.getOnlineUsers(page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/active/count")
    public ActiveUsersResponse getActiveUsersCount() {
        return sessionService.activeUsersCount();
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/active")
    public Page<User> getActiveUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return sessionService.getActiveUsers(page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/new/count")
    public NewUsersResponse getNewUsersCount() {
        return userService.newUsersCount();
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/new")
    public Page<User> getNewUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.getNewUsers(page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/all/count")
    public AllUsersResponse getAllUsersCount() {
        return userService.allUsersCount();
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/all")
    public Page<User> getAllUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.findAll(page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PostMapping(value = "/private/users/{userId}/mail/confirmed")
    public SuccessResponse sendMailConfirmed(@PathVariable UUID userId) {
        return userService.sendMailConfirmed(userId);
    }

}
