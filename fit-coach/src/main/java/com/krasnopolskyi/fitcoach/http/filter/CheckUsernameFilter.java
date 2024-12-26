package com.krasnopolskyi.fitcoach.http.filter;

import com.krasnopolskyi.fitcoach.entity.Role;
import com.krasnopolskyi.fitcoach.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j(topic = "FIT-COACH-CHECK-USERNAME")
@Component
@RequiredArgsConstructor
@Order(2)
public class CheckUsernameFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final List<String> FREE_PATHS = Arrays.asList(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**"
//            "/api/v1/fit-coach/training-types" // Allow this end-point for creating Front-end part
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // allow access to free path
        if (isExcludedPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String token;

        // missing token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleExpiredTokenException(response, "Token missing", HttpStatus.UNAUTHORIZED);
            return;
        }

        token = authHeader.substring(7);


        List<Role> roles = jwtService.extractRoles(token);
        // allow access for SERVICE role
        if (roles.contains(Role.SERVICE)) {
            filterChain.doFilter(request, response);
            return;
        }


        String authenticatedUsername = jwtService.extractUserName(token);
        String inRequestUsername = extractUsernameFromRequest(request);

        // allow access if only username in request and in token are the same
        if (inRequestUsername != null &&
                authenticatedUsername != null &&
                !authenticatedUsername.equals(inRequestUsername)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{ \"status\": " + HttpStatus.FORBIDDEN.value() + "," +
                    "\"message\": \"You do not have the necessary permissions to access this resource.\"}");
            log.warn("authenticated user" + authenticatedUsername + " tried to access resource: " + requestPath);
        } else {
            filterChain.doFilter(request, response); // Allow the request to proceed
        }
    }

    public static boolean isExcludedPath(String requestPath) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return FREE_PATHS.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    private String extractUsernameFromRequest(HttpServletRequest request) {
        String usernameInRequest = null;

        // If username is part of the URL (e.g., /trainee/{username}/update)
        if (request.getRequestURI().contains("/api/v1/fit-coach/trainees") ||
                request.getRequestURI().contains("/api/v1/fit-coach/trainers")) {
            String[] parts = request.getRequestURI().split("/");
            usernameInRequest = parts[5];
            log.info("CHECK in FILTER: ===== " + usernameInRequest);
        }

        return usernameInRequest;
    }

    private void handleExpiredTokenException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{ \"status\": " + status.value() + "," +
                "\"message\": \"" + message + "\"}");
    }
}
