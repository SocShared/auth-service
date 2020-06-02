package ml.socshared.auth.exception.impl;

import ml.socshared.auth.exception.AbstractRestHandleableException;
import org.springframework.http.HttpStatus;

public class HttpServiceUnauthorizedException extends AbstractRestHandleableException {
    public HttpServiceUnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED);
    }

    public HttpServiceUnauthorizedException(HttpStatus httpStatus) {
        super(httpStatus);
    }

    public HttpServiceUnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
