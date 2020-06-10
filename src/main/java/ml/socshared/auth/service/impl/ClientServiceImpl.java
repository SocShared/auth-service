package ml.socshared.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.auth.domain.model.ClientModel;
import ml.socshared.auth.domain.request.NewClientRequest;
import ml.socshared.auth.domain.response.ClientResponse;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.entity.Client;
import ml.socshared.auth.entity.Role;
import ml.socshared.auth.entity.User;
import ml.socshared.auth.entity.base.Status;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.repository.ClientRepository;
import ml.socshared.auth.repository.RoleRepository;
import ml.socshared.auth.repository.UserRepository;
import ml.socshared.auth.service.ClientService;
import ml.socshared.auth.service.sentry.SentrySender;
import ml.socshared.auth.service.sentry.SentryTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SentrySender sentrySender;

    @Override
    public ClientResponse add(UUID userId, NewClientRequest request) {
        log.info("saving -> {}", request);

        Client client = new Client();
        client.setAccessType(request.getAccessType());
        client.setName(request.getName());
        client.setValidRedirectUri(request.getValidRedirectUri());
        client.setClientSecret(UUID.randomUUID());;

        User user = userRepository.findById(userId).orElseThrow(() -> new HttpNotFoundException("Not found user by id: " + userId));
        client.setUser(user);
        Role role = roleRepository.findByName("CONTENT_MANAGER").orElse(null);
        if (role != null) {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            client.setRoles(roles);
        }

        ClientResponse response = new ClientResponse(clientRepository.save(client));

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_id", userId);
        additionalData.put("name_client", response.getName());
        additionalData.put("client_id", response.getClientId());
        sentrySender.sentryMessage("add client", additionalData, Collections.singletonList(SentryTag.ADD_CLIENT));

        return response;
    }

    @Override
    public ClientResponse update(UUID userId, UUID id, NewClientRequest request) {
        log.info("saving -> {}", request);

        Client client = clientRepository.findByClientIdAndUserId(UUID.fromString(id.toString()), userId)
                .orElseThrow(() -> new HttpNotFoundException("Not found client by id: " + id));

        client.setName(request.getName());
        client.setAccessType(request.getAccessType());
        client.setValidRedirectUri(request.getValidRedirectUri());

        ClientResponse response = new ClientResponse(clientRepository.save(client));

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_id", userId);
        additionalData.put("name_client", response.getName());
        additionalData.put("client_id", response.getClientId());
        sentrySender.sentryMessage("update client", additionalData, Collections.singletonList(SentryTag.UPDATE_CLIENT));

        return response;
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
        Client client = clientRepository.findByClientIdAndClientSecret(UUID.fromString(request.getClientId()),
                UUID.fromString(request.getClientSecret())).orElse(null);

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(client != null);

        return successResponse;
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

    @Override
    public ClientResponse findByUserIdAndClientId(UUID userId, UUID clientId) {
        return new ClientResponse(clientRepository.findByClientIdAndUserId(clientId, userId)
                .orElseThrow(() -> new HttpNotFoundException("Not found by user id and client id")));
    }

    @Override
    public Page<ClientModel> findByUserId(UUID userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ClientModel> clients = clientRepository.findByUserId(userId, pageable);

        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("user_id", userId);
        sentrySender.sentryMessage("get clients by user id " + userId, additionalData, Collections.singletonList(SentryTag.GET_CLIENT_BY_USER));

        return clients;
    }
}
