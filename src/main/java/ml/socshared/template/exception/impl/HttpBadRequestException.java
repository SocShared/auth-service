package ml.socshared.template.exception.impl;

import ml.socshared.template.exception.AbstractRestHandleableException;
import ml.socshared.template.exception.SocsharedErrors;
import org.springframework.http.HttpStatus;

public class HttpBadRequestException extends AbstractRestHandleableException {
    public HttpBadRequestException() {
        super(SocsharedErrors.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public HttpBadRequestException(SocsharedErrors errorCode, HttpStatus httpStatus) {
        super(errorCode, httpStatus);
    }

    public HttpBadRequestException(String message) {
        super(message, SocsharedErrors.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }
}