package in.divvyup.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, "unauthorized_access", message);
    }
}
