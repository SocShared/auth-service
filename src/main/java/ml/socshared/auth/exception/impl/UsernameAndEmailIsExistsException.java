package ml.socshared.auth.exception.impl;

public class UsernameAndEmailIsExistsException extends RegistrationException {
    public UsernameAndEmailIsExistsException() {
        super("username and email is exist");
    }

    public UsernameAndEmailIsExistsException(String message) {
        super(message);
    }

    public UsernameAndEmailIsExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAndEmailIsExistsException(Throwable cause) {
        super(cause);
    }

    protected UsernameAndEmailIsExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
