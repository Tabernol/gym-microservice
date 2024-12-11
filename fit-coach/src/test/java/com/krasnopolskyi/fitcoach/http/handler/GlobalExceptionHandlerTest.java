package com.krasnopolskyi.fitcoach.http.handler;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private HttpHeaders headers;

    @Mock
    private BindingResult bindingResult;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }



    @Test
    void handleAllUncaughtException_ShouldReturnInternalServerError() {
        // Arrange
        Exception exception = new Exception("An error occurred");

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleAllUncaughtException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Sorry, but something went wrong. Try again later", errorResponse.getMessage());
    }

    @Test
    void handleNoSuchElementFoundException_ShouldReturnNotFound() {
        // Arrange
        EntityException exception = new EntityException("Entity not found");

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleNoSuchElementFoundException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Entity not found", errorResponse.getMessage());
    }

    @Test
    void handleAuthnException_ShouldReturnForbidden() {
        // Arrange
        AuthnException exception = new AuthnException("Authentication failed");
        exception.setCode(403);

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleAuthnException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Authentication failed", errorResponse.getMessage());
    }

    @Test
    void handleValidateException() {
        // Arrange
        ValidateException exception = new ValidateException("Validation failed");

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleCustomValidateException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Validation failed", errorResponse.getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValid_withSingleFieldError() {
        // Arrange: Prepare a mock validation error for the 'firstName' field
        FieldError fieldError = new FieldError("traineeDto", "firstName", "First name can't be null");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act: Call the handler method
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(
                methodArgumentNotValidException, headers, HttpStatus.UNPROCESSABLE_ENTITY, webRequest);

        // Assert: Verify the response structure and content
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorResponse.getMessage()).isEqualTo("Validation error. Check 'errors' field for details.");
        assertThat(errorResponse.getErrors()).hasSize(1);
    }

    @Test
    void testHandleMethodArgumentNotValid_withMultipleFieldErrors() {
        // Arrange: Prepare multiple mock validation errors for 'firstName' and 'lastName' fields
        FieldError firstNameError = new FieldError("traineeDto", "firstName", "First name can't be null");
        FieldError lastNameError = new FieldError("traineeDto", "lastName", "Last name can't be null");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(firstNameError, lastNameError));

        // Act: Call the handler method
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(
                methodArgumentNotValidException, headers, HttpStatus.UNPROCESSABLE_ENTITY, webRequest);

        // Assert: Verify the response structure and content
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isInstanceOf(ErrorResponse.class);

        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorResponse.getMessage()).isEqualTo("Validation error. Check 'errors' field for details.");
        assertThat(errorResponse.getErrors()).hasSize(2);
    }


    @Test
    void handleAccessDeniedException() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleAccessDeniedException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("You do not have the necessary permissions to access this resource.", errorResponse.getMessage());
    }
}
