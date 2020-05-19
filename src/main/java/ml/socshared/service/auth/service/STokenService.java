package ml.socshared.service.auth.service;

import ml.socshared.service.auth.domain.request.CheckTokenRequest;
import ml.socshared.service.auth.domain.request.ServiceTokenRequest;
import ml.socshared.service.auth.domain.response.ServiceTokenResponse;
import ml.socshared.service.auth.domain.response.SuccessResponse;

public interface STokenService {

    ServiceTokenResponse getToken(ServiceTokenRequest request);
    SuccessResponse checkValidateToken(CheckTokenRequest request);

}
