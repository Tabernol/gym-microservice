package com.krasnopolskyi.fitcoach.http.filter;

import com.krasnopolskyi.fitcoach.entity.Role;
import com.krasnopolskyi.fitcoach.service.JwtService;
import com.krasnopolskyi.fitcoach.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    private static final List<String> FREE_PATHS = Arrays.asList(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**"
//            "/api/v1/fit-coach/training-types" // Allow this end-point for creating Front-end part
    );


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        log.info("req path " + requestPath);

        // Bypass JWT filter for excluded paths
        if (isExcludedPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;

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
                log.info("User roles: " + userRoles);

                // Check if the user is authorized for the requested path
                if (!isAuthorizedForPath(userRoles, requestPath)) {
                    handleExpiredTokenException(response, "Access denied", HttpStatus.FORBIDDEN);
                    return;
                }

                // Continue the request if token is valid and authorized
                filterChain.doFilter(request, response);
            } else {
                log.info("Token invalid");
                handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
                return;
            }
            // Continue the request if token is valid
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            log.error("JWT token is invalid. ", e);
            handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED); // Handle the exception
        }
    }

    public static boolean isExcludedPath(String requestPath) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return FREE_PATHS.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    // Method to check if the user's roles allow access to the requested path
    private boolean isAuthorizedForPath(List<Role> userRoles, String requestPath) {
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // Trainee role checks
        if (userRoles.contains(Role.TRAINEE)) {
            if (
                    pathMatcher.match("/api/v1/fit-coach/trainees/**", requestPath) ||
                    pathMatcher.match("/api/v1/fit-coach/trainings/**", requestPath) ||
                    pathMatcher.match("/api/v1/fit-coach/training-types/**", requestPath)) {
                return true;
            }
        }

        // Trainer role checks
        if (userRoles.contains(Role.TRAINER)) {
            if (
                    pathMatcher.match("/api/v1/fit-coach/trainers/**", requestPath) ||
                    pathMatcher.match("/api/v1/fit-coach/trainings/**", requestPath) ||
                    pathMatcher.match("/api/v1/fit-coach/training-types/**", requestPath)) {
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
