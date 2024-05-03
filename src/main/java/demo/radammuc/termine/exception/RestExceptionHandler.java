package demo.radammuc.termine.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleAccessDeniedException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                "Entity not found", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler({OptimisticLockException.class})
    public ResponseEntity<Object> handleOptimisticLockException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                "Entity not found", new HttpHeaders(), HttpStatus.CONFLICT);
    }
}
