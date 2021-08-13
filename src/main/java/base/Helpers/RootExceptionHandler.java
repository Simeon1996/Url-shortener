package base.Helpers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.naming.LimitExceededException;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.NoSuchElementException;

// @TODO use custom exceptions where possible
@ControllerAdvice
public class RootExceptionHandler {

    @ExceptionHandler(value = { NoSuchElementException.class })
    public ResponseEntity<ErrorMessage> handleNoSuchElementException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = buildErrorMessage(HttpStatus.NOT_FOUND, ex, request);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { MalformedURLException.class })
    public ResponseEntity<ErrorMessage> handleMalformedUrlException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = buildErrorMessage(HttpStatus.NOT_ACCEPTABLE, ex, request);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = { InvalidParameterException.class })
    public ResponseEntity<ErrorMessage> handleInvalidParameterException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = buildErrorMessage(HttpStatus.BAD_REQUEST, ex, request);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { IllegalStateException.class, LimitExceededException.class})
    public ResponseEntity<ErrorMessage> handleIllegalStateException(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = buildErrorMessage(HttpStatus.CONFLICT, ex, request);
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    private ErrorMessage buildErrorMessage(HttpStatus status, Exception ex, WebRequest request) {
        return new ErrorMessage(status.value(), new Date(), ex.getMessage(), request.getDescription(false));
    }
}
