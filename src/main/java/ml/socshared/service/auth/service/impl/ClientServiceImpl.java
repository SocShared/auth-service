package ml.socshared.service.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.model.ClientModel;
import ml.socshared.service.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.service.auth.domain.request.NewClientRequest;
import ml.socshared.service.auth.domain.response.ClientResponse;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.Client;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.entity.User;
import ml.socshared.service.auth.entity.base.Status;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.repository.ClientRepository;
import ml.socshared.service.auth.repository.RoleRepository;
import ml.socshared.service.auth.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;

    public ClientServiceImpl(ClientRepository clientRepository, RoleRepository roleRepository) {
        this.clientRepository = clientRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public ClientResponse add(NewClientRequest request) {
        log.info("saving -> {}", request);

        Client client = new Client();
        client.setAccessType(request.getAccessType());
        client.setName(request.getName());
        client.setValidRedirectUri(request.getValidRedirectUri());
        client.setClientSecret(UUID.randomUUID());;
        return new ClientResponse(clientRepository.save(client));
    }

    @Override
    public ClientResponse update(UUID id, NewClientRequest request) {
        log.info("saving -> {}", request);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found client by id: " + id));

        client.setName(request.getName());
        client.setAccessType(request.getAccessType());
        client.setValidRedirectUri(request.getValidRedirectUri());

        return new ClientResponse(clientRepository.save(client));
    }

    @Override
    public ClientResponse activation(UUID id) {
        log.info("activation by id -> {}", id);

        return new ClientResponse(clientRepository.setStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id)));
    }

    @Override
    public ClientResponse deactivation(UUID id) {
        log.info("deactivating by id -> {}", id);

        return new ClientResponse(clientRepository.setStatus(id, Status.NOT_ACTIVE)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id)));
    }

    @Override
    public ClientResponse deleteById(UUID id) {
        log.info("deleting by id -> {}", id);

        return new ClientResponse(clientRepository.setStatus(id, Status.DELETE)
                .orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + id)));
    }

    @Override
    public ClientResponse findById(UUID id) {
        log.info("find by id -> {}", id);
        return new ClientResponse(clientRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found client by id: " + id)));
    }

    @Override
    public Page<ClientModel> findAll(Integer page, Integer size) {
        log.info("find all");
        Pageable pageable = PageRequest.of(page, size);
        return clientRepository.findAllClients(pageable);
    }

    @Override
    public SuccessResponse checkData(ClientCredentialsRequest request) {
        log.info("checking c and password");
        Client client = clientRepository.findByClientIdAndClientSecret(request.getClientId(), request.getClientSecret()).orElse(null);

        return SuccessResponse.builder().success(client != null).build();
    }

    @Override
    public ClientResponse addRole(UUID id, UUID roleId) {
        log.info("adding role -> {}", roleId);

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new HttpNotFoundException("Not found role by id: " + roleId));

        Client client = clientRepository.findById(id).orElseThrow(() -> new HttpNotFoundException("Not found client by id: " + id));

        client.getRoles().add(role);
        client = clientRepository.save(client);

        return new ClientResponse(client);
    }

    @Override
    public ClientResponse removeRole(UUID id, UUID roleId) {
        log.info("removing role -> {}", roleId);

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new HttpNotFoundException("Not found role by id: " + roleId));

        Client client = clientRepository.findById(id).orElseThrow(() -> new HttpNotFoundException("Not found client by id: " + id));

        client.getRoles().remove(role);
        client = clientRepository.save(client);

        return new ClientResponse(client);
    }
}
