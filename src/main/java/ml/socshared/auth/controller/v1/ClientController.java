package ml.socshared.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.model.ClientModel;
import ml.socshared.auth.domain.request.NewClientRequest;
import ml.socshared.auth.domain.response.ClientResponse;
import ml.socshared.auth.entity.Client;
import ml.socshared.auth.exception.impl.HttpBadRequestException;
import ml.socshared.auth.service.ClientService;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
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
@RequestMapping(value = "/api/v1", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class ClientController {

    private final ClientService clientService;
    private final JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/clients")
    public Page<ClientModel> findAllClients(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                            @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return clientService.findAll(page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/clients/{clientId}")
    public ClientResponse findByClientId(@PathVariable UUID clientId) {
        return clientService.findById(clientId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/{userId}/clients/{clientId}")
    public ClientResponse findByClientIdAndUserId(@PathVariable UUID userId, @PathVariable UUID clientId) {
        return clientService.findByUserIdAndClientId(userId, clientId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @GetMapping(value = "/private/users/{userId}/clients")
    public Page<ClientModel> findByUserId(@PathVariable UUID userId,
                                          @Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                          @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return clientService.findByUserId(userId, page, size);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PostMapping(value = "/private/users/{userId}/clients")
    public ClientResponse addClient(@PathVariable UUID userId, @Valid @RequestBody NewClientRequest request) {
        return clientService.add(userId, request);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/users/{userId}/clients/{clientId}")
    public ClientResponse updateClient(@PathVariable UUID userId, @PathVariable UUID clientId,
                                       @Valid @RequestBody NewClientRequest request) {
        return clientService.update(userId, clientId, request);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @DeleteMapping(value = "/private/clients/{clientId}")
    public void deleteClient(@PathVariable UUID clientId) {
        clientService.deleteById(clientId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/clients/{clientId}/activation")
    public ClientResponse activation(@PathVariable UUID clientId) {
        return clientService.activation(clientId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/clients/{clientId}/deactivation")
    public ClientResponse deactivation(@PathVariable UUID clientId) {
        return clientService.deactivation(clientId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/clients/{clientId}/roles/{roleId}/add")
    public ClientResponse addRole(@PathVariable UUID clientId, @PathVariable UUID roleId) {
        return clientService.addRole(clientId, roleId);
    }

    @PreAuthorize("hasRole('SERVICE')")
    @PatchMapping(value = "/private/clients/{clientId}/roles/{roleId}/delete")
    public ClientResponse removeRole(@PathVariable UUID clientId, @PathVariable UUID roleId) {
        return clientService.removeRole(clientId, roleId);
    }
}
