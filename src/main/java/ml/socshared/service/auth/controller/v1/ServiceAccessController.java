package ml.socshared.service.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.service.auth.domain.request.ServiceTokenRequest;
import ml.socshared.service.auth.domain.response.ServiceTokenResponse;
import ml.socshared.service.auth.service.STokenService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class ServiceAccessController {

    private final STokenService service;

    @PostMapping(value = "/service/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    private ServiceTokenResponse getServiceToken(@Valid ServiceTokenRequest request) {
        return service.getToken(request);
    }

}
