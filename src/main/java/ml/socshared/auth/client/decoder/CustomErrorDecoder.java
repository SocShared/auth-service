package ml.socshared.auth.client.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.exception.impl.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = "Undefined error";
        try {
            message = new String(response.body().asInputStream().readAllBytes());
            log.info("Status code " + response.status() + ", methodKey = " + methodKey);
            log.info("Message: " + message);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }

        switch (response.status()) {
            case 400:
                return new HttpBadRequestException(message);
            case 404:
                return new HttpNotFoundException(message);
            case 401:
                return new HttpServiceUnauthorizedException(message);
            case 403:
                return new HttpServiceForbiddenException(message);
            default:
                return new HttpOtherExceptions(message, HttpStatus.valueOf(response.status()));
        }
    }
}
