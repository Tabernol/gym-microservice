package com.krasnopolskyi.report.http.handler;

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

/**
 * Global exception handler for handling various exceptions that may occur during API requests.
 */
@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String VALIDATION_ERROR_MESSAGE = "Validation error. Check 'errors' field for details.";
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
        // set errors message and content to request attribute for further reading in interceptor
        passMessageToControllerLogInterceptor(request, errorResponse);
        log.error("Unknown error occurred", exception);
        return ResponseEntity.internalServerError().body(errorResponse);
    }


    private void passMessageToControllerLogInterceptor(WebRequest webRequest, ErrorResponse errorResponse){
        // Cast WebRequest to ServletWebRequest to access HttpServletRequest and then has access to this attribute in interceptor
        if (webRequest instanceof ServletWebRequest) {
            HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
            // Set the error message in HttpServletRequest so that the interceptor can log it
            request.setAttribute("errorMessage", errorResponse.getMessage());
            request.setAttribute("errorContent", errorResponse.getErrors());
        }
    }
}
