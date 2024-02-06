package in.divvyup.exception;

import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import in.divvyup.controller.view.ExceptionView;

@ControllerAdvice
@SuppressWarnings ({ "unused" })
public class GlobalExceptionHandler {
    @ExceptionHandler (ApplicationException.class)
    public ResponseEntity<ExceptionView> handleException(ApplicationException exception) {
        return new ResponseEntity<>(ExceptionView.builder().errorCode(exception.getErrorCode()).errorMessage(exception.getErrorMessage()).build(),
                exception.getResponseCode());
    }

    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionView> handleConstraintViolation(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + " : " + e.getDefaultMessage())
                                       .collect(Collectors.joining(";"));
        return new ResponseEntity<>(ExceptionView.builder().errorCode("invalid_argument").errorMessage(errorMessage).build(), HttpStatus.BAD_REQUEST);
    }
}
