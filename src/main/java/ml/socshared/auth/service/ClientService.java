package ml.socshared.auth.service;

import ml.socshared.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.auth.domain.model.ClientModel;
import ml.socshared.auth.domain.request.NewClientRequest;
import ml.socshared.auth.domain.response.ClientResponse;
import ml.socshared.auth.domain.response.SuccessResponse;
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
