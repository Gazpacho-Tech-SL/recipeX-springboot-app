package recipex.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {

    var errorMessage = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("Validation failed");

    var apiError = new ApiError()
        .setError("VALIDATION_ERROR")
        .setMessage(errorMessage)
        .setPath(ex.getBindingResult().getFieldErrors().get(0).getObjectName());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
    var apiError = new ApiError()
        .setError("INVALID_ARGUMENT")
        .setMessage(ex.getMessage())
        .setPath("Argument");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
    var errorMessage = ex.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .findFirst()
        .orElse("Validation error");

    var apiError = new ApiError()
        .setError("CONSTRAINT_VIOLATION")
        .setMessage(errorMessage)
        .setPath("Constraint");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
  }
}
