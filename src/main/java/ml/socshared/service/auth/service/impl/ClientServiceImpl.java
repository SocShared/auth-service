package ml.socshared.service.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.request.AuthRequest;
import ml.socshared.service.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.service.auth.domain.request.NewClientRequest;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.entity.Client;
import ml.socshared.service.auth.entity.User;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.repository.ClientRepository;
import ml.socshared.service.auth.repository.RoleRepository;
import ml.socshared.service.auth.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

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
    public Client add(NewClientRequest request) {
        log.info("saving -> {}", request);

        Client client = new Client();
        client.setAccessType(request.getAccessType());
        client.setName(request.getName());
        client.setValidRedirectUri(request.getValidRedirectUri());

        return clientRepository.save(client);
    }

    @Override
    public Client update(UUID id, NewClientRequest request) {
        log.info("saving -> {}", request);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found client by id: " + id));

        client.setName(request.getName());
        client.setAccessType(request.getAccessType());
        client.setValidRedirectUri(request.getValidRedirectUri());

        return clientRepository.save(client);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("deleting by id -> {}", id);
        clientRepository.deleteById(id);
    }

    @Override
    public Client findById(UUID id) {
        log.info("find by id -> {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found client by id: " + id));
    }

    @Override
    public Page<Client> findAll(Integer page, Integer size) {
        log.info("find all");
        Pageable pageable = PageRequest.of(page, size);
        return clientRepository.findAll(pageable);
    }

    @Override
    public SuccessResponse checkData(ClientCredentialsRequest request) {
        log.info("checking c and password");
        Client client = clientRepository.findByClientIdAndClientSecret(UUID.fromString(request.getClientId()), request.getClientSecret()).orElse(null);

        return SuccessResponse.builder().success(client != null).build();
    }
}
