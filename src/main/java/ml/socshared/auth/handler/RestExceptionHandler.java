package ml.socshared.auth.handler;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.exception.AbstractRestHandleableException;
import ml.socshared.auth.exception.impl.AuthenticationException;
import ml.socshared.auth.exception.impl.EmailIsExistsException;
import ml.socshared.auth.exception.impl.UsernameAndEmailIsExistsException;
import ml.socshared.auth.exception.impl.UsernameIsExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<RestApiError> buildErrorResponse(Throwable exc, HttpStatus httpStatus,
                                                            ServletWebRequest webRequest) {
        return new ResponseEntity<>(new RestApiError(exc, httpStatus, webRequest), httpStatus);
    }

    @ExceptionHandler(AbstractRestHandleableException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, AbstractRestHandleableException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, exc.getHttpStatus(), webRequest);
    }

    @ExceptionHandler(UsernameAndEmailIsExistsException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, UsernameAndEmailIsExistsException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, HttpStatus.BAD_REQUEST, webRequest);
    }

    @ExceptionHandler(UsernameIsExistsException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, UsernameIsExistsException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, HttpStatus.BAD_REQUEST, webRequest);
    }

    @ExceptionHandler(EmailIsExistsException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, EmailIsExistsException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, HttpStatus.BAD_REQUEST, webRequest);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, ConstraintViolationException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, HttpStatus.BAD_REQUEST, webRequest);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, AuthenticationException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, HttpStatus.UNAUTHORIZED, webRequest);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, AccessDeniedException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, HttpStatus.FORBIDDEN, webRequest);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, Throwable exc) {
        log.error(exc.getMessage());
        exc.printStackTrace();
        return buildErrorResponse(exc, HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }
}

