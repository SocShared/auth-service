package ml.socshared.service.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.request.CheckTokenRequest;
import ml.socshared.service.auth.domain.request.ServiceTokenRequest;
import ml.socshared.service.auth.domain.response.ServiceTokenResponse;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import ml.socshared.service.auth.entity.SocsharedService;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.repository.SocsharedServiceRepository;
import ml.socshared.service.auth.service.STokenService;
import ml.socshared.service.auth.service.jwt.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class STokenServiceImpl implements STokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final SocsharedServiceRepository serviceRepository;

    @Override
    public ServiceTokenResponse getToken(ServiceTokenRequest request) {
        log.info("checking data service to");

        serviceRepository.findByServiceIdAndServiceSecret(request.getToServiceId(), request.getToSecretService())
                .orElseThrow(() -> new HttpNotFoundException("Not found service by service id and secret service"));

        return jwtTokenProvider.buildServiceToken(request);
    }

    @Override
    public SuccessResponse checkValidateToken(CheckTokenRequest request) {
        log.info("checking data service to");

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(jwtTokenProvider.validateServiceToken(request));

        return successResponse;
    }
}
