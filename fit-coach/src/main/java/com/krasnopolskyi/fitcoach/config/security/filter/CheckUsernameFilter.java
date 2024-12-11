package com.krasnopolskyi.fitcoach.config.security.filter;

import com.krasnopolskyi.fitcoach.config.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class CheckUsernameFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        if (SecurityConfig.isExcludedPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authenticatedUsername = getAuthenticatedUsername();
        String inRequestUsername = extractUsernameFromRequest(request);

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

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName(); // Assuming username is the principal's name
        }
        return null;
    }

    private String extractUsernameFromRequest(HttpServletRequest request) {
        String usernameInRequest = null;

        // If username is part of the URL (e.g., /trainee/{username}/update)
        if (request.getRequestURI().contains("/api/v1/trainees") ||
                request.getRequestURI().contains("/api/v1/trainers")) {
            String[] parts = request.getRequestURI().split("/");
            usernameInRequest = parts[4];
        }

        return usernameInRequest;
    }
}
