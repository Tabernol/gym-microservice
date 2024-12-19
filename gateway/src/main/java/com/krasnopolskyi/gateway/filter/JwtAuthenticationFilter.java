package com.krasnopolskyi.gateway.filter;

import com.krasnopolskyi.gateway.service.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    private static final List<String> FREE_PATHS = Arrays.asList(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/v1/fit-coach/auth/login",
            "/api/v1/fit-coach/auth/logout",
            "/api/v1/fit-coach/auth/sign-up/**"
            // "/api/v1/fit-coach/training-types" // Allow this end-point for creating Front-end part
    );




    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        // Bypass JWT filter for excluded paths
        if (isExcludedPath(requestPath)) {
            return chain.filter(exchange);
        }

        // Extract the Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return handleException(exchange, "Token missing", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            // Validate the token
            if (jwtService.isTokenValid(token)) {
                log.info("Token is valid. Forwarding request to downstream service.");
                // Add token validation logic or user authentication setting here if needed
            } else {
                return handleException(exchange, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
            }
            // Continue the request if token is valid
            return chain.filter(exchange);

        } catch (JwtException e) {
            log.error("JWT token is invalid: ", e);
            return handleException(exchange, "JWT token is invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    // Utility method to check if the request path should bypass
    public static boolean isExcludedPath(String requestPath) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return FREE_PATHS.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    private Mono<Void> handleException(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorMessage = "{ \"status\": " + status.value() + ", \"message\": \"" + message + "\" }";
        DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
