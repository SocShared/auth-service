package ml.socshared.service.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.service.auth.domain.model.UserModel;
import ml.socshared.service.auth.domain.request.NewUserRequest;
import ml.socshared.service.auth.domain.request.UpdatePasswordRequest;
import ml.socshared.service.auth.domain.request.UpdateUserRequest;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.repository.UserRepository;
import ml.socshared.service.auth.service.UserService;
import ml.socshared.service.auth.service.jwt.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ml.socshared.service.auth.api.v1.rest.UserApi;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1")
@Validated
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService service;
    private final JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/protected/admin/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<UserModel> findAll(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                   @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return service.findAll(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/users/{userId}/activation", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse activation(@PathVariable UUID userId) {
        return service.activation(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/users/{userId}/deactivation", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse deactivation(@PathVariable UUID userId) {
        return service.deactivation(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/users/{userId}/roles/{roleId}/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse addRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return service.addRole(userId, roleId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/users/{userId}/roles/{roleId}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse removeRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return service.removeRole(userId, roleId);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @DeleteMapping(value = "/protected/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(HttpServletRequest request) {
        UUID userId = jwtTokenProvider.getUserIdByToken(jwtTokenProvider.resolveToken(request));
        service.deleteById(userId);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PatchMapping(value = "/protected/users", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updateData(@Valid @RequestBody UpdateUserRequest request, HttpServletRequest servletRequest) {
        UUID userId = jwtTokenProvider.getUserIdByToken(jwtTokenProvider.resolveToken(servletRequest));
        return service.update(userId, request);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PatchMapping(value = "/protected/users/password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updatePassword(@Valid @RequestBody UpdatePasswordRequest request, HttpServletRequest servletRequest) {
        UUID userId = jwtTokenProvider.getUserIdByToken(jwtTokenProvider.resolveToken(servletRequest));
        return service.updatePassword(userId, request);
    }

}
