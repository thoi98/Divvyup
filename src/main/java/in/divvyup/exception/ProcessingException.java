package in.divvyup.exception;

import org.springframework.http.HttpStatus;

public class ProcessingException extends ApplicationException {
    public ProcessingException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "server_error", message);
    }
}
