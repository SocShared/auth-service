package ml.socshared.auth.client;

import ml.socshared.auth.domain.request.SendMessageMailConfirmRequest;
import ml.socshared.auth.domain.request.SendMessageRequest;
import ml.socshared.auth.domain.response.SuccessResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "mail-sender-client", url = "${feign.url.mail:}")
public interface MailSenderClient {

    @GetMapping(value = "/api/v1/private/message", produces = MediaType.APPLICATION_JSON_VALUE)
    SuccessResponse send(@RequestBody SendMessageRequest request, @RequestHeader("Authorization") String token);

    @GetMapping(value = "/api/v1/private/message/confirm/mail", produces = MediaType.APPLICATION_JSON_VALUE)
    SuccessResponse sendMailConfirm(@RequestBody SendMessageMailConfirmRequest request, @RequestHeader("Authorization") String token);

}
