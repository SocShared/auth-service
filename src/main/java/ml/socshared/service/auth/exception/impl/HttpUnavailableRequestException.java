package ml.socshared.service.auth.exception.impl;

import ml.socshared.service.auth.exception.AbstractRestHandleableException;
import ml.socshared.service.auth.exception.SocsharedErrors;
import org.springframework.http.HttpStatus;

public class HttpUnavailableRequestException extends AbstractRestHandleableException {
    public HttpUnavailableRequestException() {
        super(SocsharedErrors.UNAVAILABLE_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpUnavailableRequestException(SocsharedErrors errorCode, HttpStatus httpStatus) {
        super(errorCode, httpStatus);
    }
}
