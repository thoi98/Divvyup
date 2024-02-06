package in.divvyup.exception;

import org.springframework.http.HttpStatus;
import in.divvyup.util.JSONUtil;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus responseCode;

    private final String errorCode;

    private final String errorMessage;

    public ApplicationException(HttpStatus responseCode, String errorCode, String errorMessage) {
        super(errorMessage);
        this.responseCode = responseCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return JSONUtil.toJson(this);
    }
}
