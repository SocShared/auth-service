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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class ClientController {

    private final ClientService clientService;
    private final JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/protected/admin/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ClientModel> findAllClients(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                            @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return clientService.findAll(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/protected/admin/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse findByClientId(@PathVariable UUID clientId) {
        return clientService.findById(clientId);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @GetMapping(value = "/protected/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse findByClientIdAndUserId(@PathVariable UUID clientId, HttpServletRequest servletRequest) {
        UUID userId = jwtTokenProvider.getUserIdByToken(jwtTokenProvider.resolveToken(servletRequest));
        return clientService.findByUserIdAndClientId(userId, clientId);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @GetMapping(value = "/protected/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ClientModel> findByUserId(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                          @Valid @NotNull @RequestParam(name = "size", required = false) Integer size,
                                          HttpServletRequest servletRequest) {
        UUID userId = jwtTokenProvider.getUserIdByToken(jwtTokenProvider.resolveToken(servletRequest));
        return clientService.findByUserId(userId, page, size);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PostMapping(value = "/protected/clients", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse addClient(@Valid @RequestBody NewClientRequest request, HttpServletRequest servletRequest) {
        UUID userId = jwtTokenProvider.getUserIdByToken(jwtTokenProvider.resolveToken(servletRequest));

        if (request.getAccessType() == Client.AccessType.CONFIDENTIAL || request.getAccessType() == Client.AccessType.PUBLIC) {
            if (request.getValidRedirectUri() == null || request.getValidRedirectUri().isEmpty()) {
                throw new HttpBadRequestException("valid redirect uri: " + request.getValidRedirectUri());
            }
        }

        return clientService.add(userId, request);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @PatchMapping(value = "/protected/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse updateClient(@PathVariable UUID clientId, @Valid @RequestBody NewClientRequest request, HttpServletRequest servletRequest) {
        UUID userId = jwtTokenProvider.getUserIdByToken(jwtTokenProvider.resolveToken(servletRequest));

        if (request.getAccessType() == Client.AccessType.CONFIDENTIAL || request.getAccessType() == Client.AccessType.PUBLIC) {
            if (request.getValidRedirectUri() == null || request.getValidRedirectUri().isEmpty()) {
                throw new HttpBadRequestException("valid redirect uri: " + request.getValidRedirectUri());
            }
        }
        return clientService.update(userId, clientId, request);
    }

    @PreAuthorize("hasRole('CONTENT_MANAGER')")
    @DeleteMapping(value = "/protected/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteClient(@PathVariable UUID clientId) {
        clientService.deleteById(clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/clients/{clientId}/activation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse activation(@PathVariable UUID clientId) {
        return clientService.activation(clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/clients/{clientId}/deactivation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse deactivation(@PathVariable UUID clientId) {
        return clientService.deactivation(clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/clients/{clientId}/roles/{roleId}/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse addRole(@PathVariable UUID clientId, @PathVariable UUID roleId) {
        return clientService.addRole(clientId, roleId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/protected/admin/clients/{clientId}/roles/{roleId}/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse removeRole(@PathVariable UUID clientId, @PathVariable UUID roleId) {
        return clientService.removeRole(clientId, roleId);
    }
}
