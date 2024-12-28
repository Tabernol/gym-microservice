package com.krasnopolskyi.gateway.handler;


import com.krasnopolskyi.gateway.exception.AuthnException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

/**
 * Global exception handler for handling various exceptions that may occur during API requests.
 */
@Slf4j(topic = "GATEWAY_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, but something went wrong. Try again later";

    /**
     * Handles unknown exceptions and builds a response with a generic internal server error message.
     *
     * @param exception The unknown exception.
     * @param request   The current web request.
     * @return ResponseEntity with a generic internal server error response.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR_MESSAGE);

        log.error("Unknown error occurred", exception);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleRunTimeException(Exception exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR_MESSAGE);

        log.error("Unknown Runtime error occurred", exception);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(AuthnException.class)
    public ResponseEntity<Object> handleAuthnException(
            AuthnException exception, WebRequest request) {

        log.warn("Authentication problem ", exception);
        return buildErrorResponse(exception, HttpStatus.valueOf(exception.getCode()), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        // Log the access denied exception
        log.warn("Access denied: {}", ex.getMessage());

        // Return a custom response for access denied
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponse(HttpStatus.FORBIDDEN.value(), "You do not have the necessary permissions to access this resource.")
        );
    }

    /**
     * Builds a generic error response based on the provided exception, HTTP status, and web request.
     *
     * @param exception  The exception.
     * @param httpStatus The HTTP status code for the response.
     * @param request    The current web request.
     * @return ResponseEntity with a generic error response.
     */
    protected ResponseEntity<Object> buildErrorResponse(Exception exception,
                                                        HttpStatus httpStatus,
                                                        WebRequest request) {
        return ResponseEntity.status(httpStatus).body(
                new ErrorResponse(httpStatus.value(), exception.getMessage()));
    }
}
