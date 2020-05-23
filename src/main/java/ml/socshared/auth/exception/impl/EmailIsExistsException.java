package ml.socshared.auth.exception.impl;

public class EmailIsExistsException extends RegistrationException {
    public EmailIsExistsException() {
        super("email is exist");
    }

    public EmailIsExistsException(String message) {
        super(message);
    }

    public EmailIsExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailIsExistsException(Throwable cause) {
        super(cause);
    }

    protected EmailIsExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
