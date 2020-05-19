package ml.socshared.service.auth.client;

import ml.socshared.service.auth.domain.request.SendMessageRequest;
import ml.socshared.service.auth.domain.response.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "mail-sender-client", url = "${feign.url.mail:}")
public interface MailSenderClient {

    @GetMapping(value = "/api/v1/message", produces = MediaType.APPLICATION_JSON_VALUE)
    SuccessResponse send(SendMessageRequest request);

}
