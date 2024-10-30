package recipex.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @Test
  void handleValidationExceptions_shouldReturnBadRequest() {
    var bindingResult = mock(BindingResult.class);
    var fieldError = mock(FieldError.class);

    when(fieldError.getDefaultMessage()).thenReturn("Validation failed");
    when(fieldError.getObjectName()).thenReturn("field");

    when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

    var ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    var response = exceptionHandler.handleValidationExceptions(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("VALIDATION_ERROR", response.getBody().getError());
    assertEquals("Validation failed", response.getBody().getMessage());
    assertEquals("field", response.getBody().getPath());
  }

  @Test
  void handleIllegalArgumentException_shouldReturnBadRequest() {
    var ex = new IllegalArgumentException("Invalid argument");
    var response = exceptionHandler.handleIllegalArgumentException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INVALID_ARGUMENT", response.getBody().getError());
    assertEquals("Invalid argument", response.getBody().getMessage());
    assertEquals("Argument", response.getBody().getPath());
  }

  @Test
  void handleConstraintViolationException_shouldReturnBadRequest() {
    var violation = mock(ConstraintViolation.class);

    when(violation.getMessage()).thenReturn("Validation error");

    HashSet<ConstraintViolation<?>> violations = new HashSet<>();
    violations.add(violation);

    var ex = new ConstraintViolationException(violations);
    var response = exceptionHandler.handleConstraintViolationException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("CONSTRAINT_VIOLATION", response.getBody().getError());
    assertEquals("Validation error", response.getBody().getMessage());
    assertEquals("Constraint", response.getBody().getPath());
  }
}