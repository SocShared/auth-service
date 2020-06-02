package ml.socshared.auth.exception.impl;

import ml.socshared.auth.exception.AbstractRestHandleableException;
import org.springframework.http.HttpStatus;

public class HttpOtherExceptions extends AbstractRestHandleableException {

    public HttpOtherExceptions(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
