package ml.socshared.service.auth.service;

import ml.socshared.service.auth.domain.request.ServiceTokenRequest;
import ml.socshared.service.auth.domain.response.ServiceTokenResponse;

public interface STokenService {

    ServiceTokenResponse getToken(ServiceTokenRequest request);

}
