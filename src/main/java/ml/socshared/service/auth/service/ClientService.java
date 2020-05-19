package ml.socshared.service.auth.service;

import ml.socshared.service.auth.domain.model.ClientModel;
import ml.socshared.service.auth.domain.request.AuthRequest;
import ml.socshared.service.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.service.auth.domain.request.NewClientRequest;
import ml.socshared.service.auth.domain.response.ClientResponse;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.entity.Client;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ClientService {

    ClientResponse add(UUID userId, NewClientRequest request);
    ClientResponse update(UUID userId, UUID id, NewClientRequest request);
    ClientResponse activation(UUID id);
    ClientResponse deactivation(UUID id);
    ClientResponse deleteById(UUID id);
    ClientResponse findById(UUID id);
    ClientResponse findByUserIdAndClientId(UUID userId, UUID clientId);
    Page<ClientModel> findByUserId(UUID userId, Integer page, Integer size);
    ClientResponse addRole(UUID clientId, UUID roleId);
    ClientResponse removeRole(UUID clientId, UUID roleId);
    Page<ClientModel> findAll(Integer page, Integer size);
    SuccessResponse checkData(ClientCredentialsRequest request);

}
