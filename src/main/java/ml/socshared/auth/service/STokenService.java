package ml.socshared.auth.service;

import ml.socshared.auth.domain.request.CheckTokenRequest;
import ml.socshared.auth.domain.request.ServiceTokenRequest;
import ml.socshared.auth.domain.response.ServiceTokenResponse;
import ml.socshared.auth.domain.response.SuccessResponse;

public interface STokenService {

    ServiceTokenResponse getToken(ServiceTokenRequest request);
    SuccessResponse checkValidateToken(CheckTokenRequest request);

}
