package ml.socshared.auth.exception.impl;

import ml.socshared.auth.exception.AbstractRestHandleableException;
import org.springframework.http.HttpStatus;

public class HttpServiceForbiddenException extends AbstractRestHandleableException {

    public HttpServiceForbiddenException() {
        super(HttpStatus.FORBIDDEN);
    }

    public HttpServiceForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public HttpServiceForbiddenException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public HttpServiceForbiddenException(HttpStatus httpStatus) {
        super(httpStatus);
    }

    public HttpServiceForbiddenException(Throwable throwable, HttpStatus httpStatus) {
        super(throwable, httpStatus);
    }
}
