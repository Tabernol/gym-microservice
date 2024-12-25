package com.krasnopolskyi.gateway.filter;

import com.krasnopolskyi.gateway.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private WebFilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockServerWebExchange exchange;
    private MockServerHttpResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        response = new MockServerHttpResponse();
    }

    @Test
    public void testFilter_ExcludedPath() {
        // Create a mock exchange with an excluded path (e.g., /swagger-ui/index.html)
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/swagger-ui/index.html").build());

        // Mock filter chain to return an empty Mono
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Call the filter
        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        // Verify the filter chain is called, meaning the request was bypassed
        verify(filterChain).filter(exchange);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFilter_MissingAuthorizationHeader() {
        // Create a mock exchange without Authorization header
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/fit-coach/trainees").build());

        // Call the filter
        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        // Verify that the response is 401 Unauthorized
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{ \"status\": 401, \"message\": \"Token missing\" }", getResponseBody(response));
    }

    @Test
    public void testFilter_InvalidToken() {
        // Create a mock exchange with an invalid token
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/fit-coach/trainees")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid_token")
                .build());

        // Mock JwtService to return false for invalid token
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        // Call the filter
        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        // Verify that the response is 401 Unauthorized
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{ \"status\": 401, \"message\": \"JWT token is expired or invalid\" }", getResponseBody(response));
    }

    @Test
    public void testFilter_ValidToken() {
        // Create a mock exchange with a valid token
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/fit-coach/trainees")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid_token")
                .build());

        // Mock JwtService to return true for a valid token
        when(jwtService.isTokenValid("valid_token")).thenReturn(true);

        // Mock filter chain to return an empty Mono (indicating the request proceeds)
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Call the filter
        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        // Verify that the filter chain is called and completes successfully
        verify(filterChain).filter(exchange);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFilter_LogoutEndpoint_WithValidToken() {
        // Create a mock exchange with the logout endpoint and a valid token
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/fit-coach/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid_token")
                .build());

        // Mock JwtService to return true for a valid token
        when(jwtService.isTokenValid("valid_token")).thenReturn(true);

        // Mock adding token to blacklist on logout
        doNothing().when(jwtService).addToBlackList("valid_token");

        // Mock filter chain to return an empty Mono
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // Call the filter
        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        // Verify that the token is added to the blacklist and the filter chain proceeds
        verify(jwtService).addToBlackList("valid_token");
        verify(filterChain).filter(exchange);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Utility to extract response body content from MockServerHttpResponse
    private String getResponseBody(MockServerHttpResponse response) {
        return response.getBodyAsString().block();
    }
}
