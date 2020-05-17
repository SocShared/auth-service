package ml.socshared.service.auth.exception.impl;

public class UsernameIsExistsException extends RegistrationException {
    public UsernameIsExistsException() {
        super("username is exist");
    }

    public UsernameIsExistsException(String message) {
        super(message);
    }

    public UsernameIsExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameIsExistsException(Throwable cause) {
        super(cause);
    }

    protected UsernameIsExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
