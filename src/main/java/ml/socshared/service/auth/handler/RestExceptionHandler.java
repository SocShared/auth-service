package ml.socshared.service.auth.handler;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.entity.SocsharedService;
import ml.socshared.service.auth.exception.AbstractRestHandleableException;
import ml.socshared.service.auth.exception.SocsharedErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                                                            ServletWebRequest webRequest, SocsharedErrors errorCode) {
        return new ResponseEntity<>(new RestApiError(exc, httpStatus, webRequest, errorCode), httpStatus);
    }

    @ExceptionHandler(AbstractRestHandleableException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, AbstractRestHandleableException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, exc.getHttpStatus(), webRequest, exc.getErrorCode());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, ConstraintViolationException exc) {
        log.error(exc.getMessage());
        return buildErrorResponse(exc, HttpStatus.BAD_REQUEST, webRequest, SocsharedErrors.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestApiError> handlePrintException(ServletWebRequest webRequest, Throwable exc) {
        log.error(exc.getMessage());
        exc.printStackTrace();
        return buildErrorResponse(exc, HttpStatus.INTERNAL_SERVER_ERROR, webRequest, SocsharedErrors.INTERNAL);
    }
}

