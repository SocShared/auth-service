package ml.socshared.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.auth.domain.request.ServiceTokenRequest;
import ml.socshared.auth.domain.response.ServiceTokenResponse;
import ml.socshared.auth.domain.request.CheckTokenRequest;
import ml.socshared.auth.domain.response.SuccessResponse;
import ml.socshared.auth.service.STokenService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class ServiceAccessController {

    private final STokenService service;

    @PostMapping(value = "/public/service/token", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ServiceTokenResponse getServiceToken(@Valid @RequestBody ServiceTokenRequest request) {
        return service.getToken(request);
    }

    @PostMapping(value = "/public/service/validate_token", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SuccessResponse checkValidateToken(@Valid @RequestBody CheckTokenRequest request) {
        return service.checkValidateToken(request);
    }

}
