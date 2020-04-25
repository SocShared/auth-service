package ml.socshared.template.exception.impl;

import ml.socshared.template.exception.AbstractRestHandleableException;
import ml.socshared.template.exception.SocsharedErrors;
import org.springframework.http.HttpStatus;

public class HttpUnavailableRequestException extends AbstractRestHandleableException {
    public HttpUnavailableRequestException() {
        super(SocsharedErrors.UNAVAILABLE_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpUnavailableRequestException(SocsharedErrors errorCode, HttpStatus httpStatus) {
        super(errorCode, httpStatus);
    }
}
