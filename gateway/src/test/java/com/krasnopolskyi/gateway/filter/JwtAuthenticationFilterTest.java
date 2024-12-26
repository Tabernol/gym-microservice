package com.krasnopolskyi.gateway.filter;

import com.krasnopolskyi.gateway.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private org.springframework.http.server.reactive.ServerHttpRequest request;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        headers = new HttpHeaders();
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(response.bufferFactory()).thenReturn(mock(org.springframework.core.io.buffer.DataBufferFactory.class));
    }

    @Test
    public void shouldBypassFilterForFreePaths() {
        when(request.getURI()).thenReturn(URI.create("/swagger-ui/index.html"));
        when(chain.filter(exchange)).thenReturn(Mono.empty());  // Return an empty Mono for the filter chain

        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Verify the Mono completes
        result.block();

        // Ensure the filter chain proceeds
        verify(chain).filter(exchange);
        // JwtService should not be called
        verifyNoInteractions(jwtService);
    }

    @Test
    public void shouldReturnUnauthorizedForMissingAuthorizationHeader() {
        when(request.getURI()).thenReturn(URI.create("/api/v1/protected"));
        when(response.writeWith(any())).thenReturn(Mono.empty());  // Return an empty Mono when writing response
        when(response.getHeaders()).thenReturn(headers);

        // Mock response buffer factory
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        when(response.bufferFactory()).thenReturn(bufferFactory);  // Mock buffer factory
        when(response.writeWith(any())).thenReturn(Mono.empty());  // Return an empty Mono when writing response

        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Verify the Mono completes
        result.block();

        // Verify that the response status is set to UNAUTHORIZED
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void shouldReturnUnauthorizedForInvalidToken() {
        when(response.getHeaders()).thenReturn(headers);
        when(request.getURI()).thenReturn(URI.create("/api/v1/protected"));
        headers.set("Authorization", "Bearer invalidToken");
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        // Mock response buffer factory
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        when(response.bufferFactory()).thenReturn(bufferFactory);  // Mock buffer factory
        when(response.writeWith(any())).thenReturn(Mono.empty());  // Return an empty Mono when writing response


        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Verify the Mono completes
        result.block();

        // Verify that the response status is set to UNAUTHORIZED
        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        // Ensure JwtService token validation is called
        verify(jwtService).isTokenValid(anyString());
    }

    @Test
    public void shouldContinueFilterForValidToken() {
        when(request.getURI()).thenReturn(URI.create("/api/v1/protected"));
        headers.set("Authorization", "Bearer validToken");
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(chain.filter(exchange)).thenReturn(Mono.empty());  // Return an empty Mono for the filter chain

        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Verify the Mono completes
        result.block();

        // Ensure the filter chain proceeds
        verify(chain).filter(exchange);
        // Ensure JwtService token validation is called
        verify(jwtService).isTokenValid(anyString());
    }

    @Test
    public void shouldBlacklistTokenOnLogout() {
        when(request.getURI()).thenReturn(URI.create("/api/v1/fit-coach/auth/logout"));
        headers.set("Authorization", "Bearer validToken");
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(chain.filter(exchange)).thenReturn(Mono.empty());  // Return an empty Mono for the filter chain

        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, chain);

        // Verify the Mono completes
        result.block();

        // Ensure the filter chain proceeds
        verify(chain).filter(exchange);
        // Ensure the token is added to blacklist
        verify(jwtService).addToBlackList(anyString());
    }
}
