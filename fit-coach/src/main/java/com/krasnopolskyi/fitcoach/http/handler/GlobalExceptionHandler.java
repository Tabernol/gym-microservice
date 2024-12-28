package com.krasnopolskyi.fitcoach.http.handler;

import com.krasnopolskyi.fitcoach.exception.AuthnException;
import com.krasnopolskyi.fitcoach.exception.EntityException;
import com.krasnopolskyi.fitcoach.exception.ValidateException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for handling various exceptions that may occur during API requests.
 */
@Slf4j(topic = "FIT_COACH_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String VALIDATION_ERROR_MESSAGE = "Validation error. Check 'errors' field for details.";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, but something went wrong. Try again later";


    /**
     * Handles validation errors and builds a response with detailed error content.
     *
     * @param ex      The exception containing validation errors.
     * @param headers The headers for the response.
     * @param status  The HTTP status code for the response.
     * @param webRequest The current web request.
     * @return ResponseEntity with a detailed error response for validation errors.
     */
    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest webRequest) {
        ErrorResponse errorResponse =
                new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), VALIDATION_ERROR_MESSAGE);
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addErrorContent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Validation error occurred: ", ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

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



    /**
     * Handles EntityException and builds a response with an appropriate status code and message.
     *
     * @param exception The EntityException.
     * @param request   The current web request.
     * @return ResponseEntity with a response for EntityException.
     */
    @ExceptionHandler({EntityException.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(
            EntityException exception, WebRequest request) {

        log.error("Failed: ", exception);
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }



    @ExceptionHandler(AuthnException.class)
    public ResponseEntity<Object> handleAuthnException(
            AuthnException exception, WebRequest request) {

        log.warn("Authentication problem ", exception);
        return buildErrorResponse(exception, HttpStatus.valueOf(exception.getCode()), request);
    }


    /**
     * Handle custom validation exception
     * @param exception
     * @param request The current web request.
     * @return ResponseEntity with a response from ValidateException.
     */
    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Object> handleCustomValidateException(
            ValidateException exception, WebRequest request) {

        log.warn("Validate exception occurred ", exception);
        return buildErrorResponse(exception, HttpStatus.UNPROCESSABLE_ENTITY, request);
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
