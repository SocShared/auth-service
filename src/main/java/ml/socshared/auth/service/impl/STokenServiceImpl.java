package ml.socshared.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.domain.request.CheckTokenRequest;
import ml.socshared.auth.domain.request.ServiceTokenRequest;
import ml.socshared.auth.domain.response.ServiceTokenResponse;
import ml.socshared.auth.entity.ServiceToken;
import ml.socshared.auth.service.STokenService;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.entity.SocsharedService;
import ml.socshared.auth.exception.impl.HttpNotFoundException;
import ml.socshared.auth.repository.ServiceTokenRepository;
import ml.socshared.auth.repository.SocsharedServiceRepository;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class STokenServiceImpl implements STokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final SocsharedServiceRepository serviceRepository;
    private final ServiceTokenRepository serviceTokenRepository;

    @Override
    public ServiceTokenResponse getToken(ServiceTokenRequest request) {
        log.info("checking data service to");

        serviceRepository.findByServiceIdAndServiceSecret(request.getToServiceId(), request.getToSecretService())
                .orElseThrow(() -> new HttpNotFoundException("Not found service by service id and secret service"));

        SocsharedService service = serviceRepository.findById(request.getFromServiceId())
                .orElseThrow(() -> new HttpNotFoundException("Not found service by service id"));

        ServiceTokenResponse serviceTokenResponse = jwtTokenProvider.buildServiceToken(request);
        ServiceToken serviceToken = new ServiceToken();
        serviceToken.setFromService(service);
        serviceToken.setToken(serviceTokenResponse.getToken());
        serviceToken.setTokenExpireIn(serviceTokenResponse.getExpireIn());
        serviceToken.setToServiceId(request.getToServiceId());

        serviceTokenRepository.findByToServiceIdAndFromServiceId(request.getToServiceId(), request.getFromServiceId())
                .ifPresent(token -> serviceTokenRepository.deleteById(token.getTokenId()));

        serviceTokenRepository.save(serviceToken);

        return serviceTokenResponse;
    }

    @Override
    public SuccessResponse checkValidateToken(CheckTokenRequest request) {
        log.info("checking data service to");

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setSuccess(jwtTokenProvider.validateServiceToken(request.getToken()));

        return successResponse;
    }
}
