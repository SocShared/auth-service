package ml.socshared.service.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.service.auth.domain.model.ClientModel;
import ml.socshared.service.auth.domain.request.NewClientRequest;
import ml.socshared.service.auth.domain.response.ClientResponse;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.Client;
import ml.socshared.service.auth.exception.impl.HttpBadRequestException;
import ml.socshared.service.auth.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;

    @GetMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ClientModel> findAllClients(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                            @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return clientService.findAll(page, size);
    }

    @GetMapping(value = "/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse findByClientId(@PathVariable UUID clientId) {
        return clientService.findById(clientId);
    }

    @PostMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse addClient(@Valid @RequestBody NewClientRequest request) {
        if (request.getAccessType() == Client.AccessType.CONFIDENTIAL || request.getAccessType() == Client.AccessType.PUBLIC) {
            if (request.getValidRedirectUri() == null || request.getValidRedirectUri().isEmpty()) {
                throw new HttpBadRequestException("valid redirect uri: " + request.getValidRedirectUri());
            }
        }
        return clientService.add(request);
    }

    @PatchMapping(value = "/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse updateClient(@PathVariable UUID clientId, @Valid @RequestBody NewClientRequest request) {
        if (request.getAccessType() == Client.AccessType.CONFIDENTIAL || request.getAccessType() == Client.AccessType.PUBLIC) {
            if (request.getValidRedirectUri() == null || request.getValidRedirectUri().isEmpty()) {
                throw new HttpBadRequestException("valid redirect uri: " + request.getValidRedirectUri());
            }
        }
        return clientService.update(clientId, request);
    }

    @DeleteMapping(value = "/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteClient(@PathVariable UUID clientId) {
        clientService.deleteById(clientId);
    }

    @PatchMapping(value = "/clients/{clientId}/activation", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse activation(@PathVariable UUID clientId) {
        return clientService.activation(clientId);
    }

    @PatchMapping(value = "/clients/{clientId}/deactivation", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse deactivation(@PathVariable UUID clientId) {
        return clientService.deactivation(clientId);
    }

    @PatchMapping(value = "/clients/{clientId}/roles/{roleId}/add", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse addRole(@PathVariable UUID clientId, @PathVariable UUID roleId) {
        return clientService.addRole(clientId, roleId);
    }

    @PatchMapping(value = "/clients/{clientId}/roles/{roleId}/delete", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientResponse removeRole(@PathVariable UUID clientId, @PathVariable UUID roleId) {
        return clientService.removeRole(clientId, roleId);
    }
}
