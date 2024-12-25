package com.krasnopolskyi.report.http.filter;

import com.krasnopolskyi.report.model.Role;
import com.krasnopolskyi.report.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j(topic = "REPORT-JWT")
@Component
@RequiredArgsConstructor
@Order(1)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        final String authHeader = request.getHeader("Authorization");
        final String token;

        // missing token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleExpiredTokenException(response, "Token missing", HttpStatus.UNAUTHORIZED);
            return;
        }

        // checking token
        token = authHeader.substring(7);
        try {

            // Validate the token
            if (jwtService.isTokenValid(token)) {

                // Extract roles from JWT
                List<Role> userRoles = jwtService.extractRoles(token);

                // Check if the user is authorized for the requested path
                if (!isAuthorizedForPath(userRoles, requestPath)) {
                    log.warn("Access denied for path: " + requestPath + " for roles: " + userRoles);
                    handleExpiredTokenException(response, "Access denied", HttpStatus.FORBIDDEN);
                    return;
                }

                // Continue the request if token is valid and authorized
                filterChain.doFilter(request, response);
            } else {
                handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
                return;
            }
        } catch (JwtException e) {
            log.error("JWT token is invalid. ", e);
            handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED); // Handle the exception
        }
    }

    // Method to check if the user's roles allow access to the requested path
    private boolean isAuthorizedForPath(List<Role> userRoles, String requestPath) {
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // Allow 'SERVICE' role for add training
        if (userRoles.contains(Role.SERVICE)) {
            if (pathMatcher.match("/api/v1/fit-coach/report/training-session", requestPath)) {
                log.info("SERVICE CALL - Access granted for add training session to report database");
                return true;
            }
        }

        if (userRoles.contains(Role.TRAINER)) {
            if (pathMatcher.match("/api/v1/fit-coach/report/generate/**", requestPath)) {
                return true;
            }
        }

        // Access denied for other paths
        return false;
    }

    private void handleExpiredTokenException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{ \"status\": " + status.value() + "," +
                "\"message\": \"" + message + "\"}");
    }
}
