package com.krasnopolskyi.gateway.handler;

import com.krasnopolskyi.gateway.exception.AuthnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

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
    void handleAccessDeniedException() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("You do not have the necessary permissions to access this resource.");

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleAccessDeniedException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("You do not have the necessary permissions to access this resource.", errorResponse.getMessage());
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
    void handleRunTimeException_ShouldReturnInternalServerError() {
        // Arrange
        RuntimeException exception = new RuntimeException("An error occurred");

        // Act
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleRunTimeException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) responseEntity.getBody();
        assertEquals("Sorry, but something went wrong. Try again later", errorResponse.getMessage());
    }
}
