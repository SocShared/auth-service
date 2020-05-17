package ml.socshared.service.auth.service;

import ml.socshared.service.auth.domain.request.AuthRequest;
import ml.socshared.service.auth.domain.request.ClientCredentialsRequest;
import ml.socshared.service.auth.domain.request.NewClientRequest;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.entity.Client;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ClientService {

    Client add(NewClientRequest request);
    Client update(UUID id, NewClientRequest request);
    void deleteById(UUID id);
    Client findById(String id);
    Page<Client> findAll(Integer page, Integer size);
    SuccessResponse checkData(ClientCredentialsRequest request);

}
