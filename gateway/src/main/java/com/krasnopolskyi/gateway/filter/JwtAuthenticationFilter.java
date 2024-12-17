package com.krasnopolskyi.gateway.filter;

import com.krasnopolskyi.gateway.config.SecurityConfig;
import com.krasnopolskyi.gateway.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        log.info("path " + requestPath);
        // Bypass JWT filter for excluded paths
        if (SecurityConfig.isExcludedPath(requestPath)) {
            log.info("Skipping JWT filter for path: " + requestPath);
            return chain.filter(exchange);
        }
        log.info("checked path " + requestPath);

        // Extract the Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return handleException(exchange, "Token missing", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            // Validate the token
            if (jwtService.isTokenValid(token)) {
                log.info("Token is valid");
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

    private Mono<Void> handleException(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorMessage = "{ \"status\": " + status.value() + ", \"message\": \"" + message + "\" }";
        DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
