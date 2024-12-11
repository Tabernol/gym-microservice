package com.krasnopolskyi.fitcoach.http.interceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ControllerLogInterceptorTest {
    private ControllerLogInterceptor controllerLogInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controllerLogInterceptor = new ControllerLogInterceptor();
    }

    @Test
    void preHandle_ShouldSetRequestIdAndLogRequest() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        when(request.getMethod()).thenReturn("GET");
        when(request.getParameterMap()).thenReturn(Map.of("param1", new String[]{"value1"}));

        // Act
        boolean result = controllerLogInterceptor.preHandle(request, response, handler);

        // Assert
        assertTrue(result);
        // Verify that request attribute was set
        verify(request).setAttribute(eq("requestId"), any(String.class));
        // Add verification for log output if needed (requires a logging framework that supports this)
    }

    @Test
    void afterCompletion_ShouldLogSuccessfulCompletion() {
        // Arrange
        String requestId = "test-request-id";
        when(request.getAttribute("requestId")).thenReturn(requestId);
        when(response.getStatus()).thenReturn(200);

        // Act
        controllerLogInterceptor.afterCompletion(request, response, handler, null);

        // Assert
        // Check log output (you may need to use a logging framework that supports this)
        verify(request, times(1)).getAttribute("requestId");
        verify(response, times(1)).getStatus();
    }

    @Test
    void afterCompletion_ShouldLogErrorMessageWhenPresent() {
        // Arrange
        String requestId = "test-request-id";
        String errorMessage = "Test error";
        when(request.getAttribute("requestId")).thenReturn(requestId);
        when(request.getAttribute("errorMessage")).thenReturn(errorMessage);
        when(response.getStatus()).thenReturn(500);

        // Act
        controllerLogInterceptor.afterCompletion(request, response, handler, new Exception("Test Exception"));

        // Assert
        // Check log output (you may need to use a logging framework that supports this)
        verify(request, times(1)).getAttribute("requestId");
        verify(response, times(1)).getStatus();
    }
}
