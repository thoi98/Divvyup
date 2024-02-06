package in.divvyup.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
@SuppressWarnings ({ "unused" })
public class InvalidRequestException extends ApplicationException {
    public InvalidRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "invalid_request", message);
    }

    public InvalidRequestException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }
}
