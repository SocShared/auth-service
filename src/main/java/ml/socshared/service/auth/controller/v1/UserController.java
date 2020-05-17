package ml.socshared.service.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.service.auth.domain.model.UserModel;
import ml.socshared.service.auth.domain.request.NewUserRequest;
import ml.socshared.service.auth.domain.request.UpdatePasswordRequest;
import ml.socshared.service.auth.domain.request.UpdateUserRequest;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.repository.UserRepository;
import ml.socshared.service.auth.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ml.socshared.service.auth.api.v1.rest.UserApi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse add(@Valid @RequestBody NewUserRequest request) {
        return service.add(request);
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<UserModel> findAll(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                   @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return service.findAll(page, size);
    }

    @PatchMapping(value = "/users/{userId}/activation", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse activation(@PathVariable UUID userId) {
        return service.activation(userId);
    }

    @PatchMapping(value = "/users/{userId}/deactivation", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse deactivation(@PathVariable UUID userId) {
        return service.deactivation(userId);
    }

    @PatchMapping(value = "/users/{userId}/roles/{roleId}/add", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse addRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return service.addRole(userId, roleId);
    }

    @PatchMapping(value = "/users/{userId}/roles/{roleId}/delete", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse removeRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        return service.removeRole(userId, roleId);
    }

    @DeleteMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse delete(@PathVariable UUID userId) {
        return service.deleteById(userId);
    }

    @PatchMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updateData(@PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest request) {
        return service.update(userId, request);
    }

    @PatchMapping(value = "/users/{userId}/password", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse updatePassword(@PathVariable UUID userId, @Valid @RequestBody UpdatePasswordRequest request) {
        return service.updatePassword(userId, request);
    }
}
