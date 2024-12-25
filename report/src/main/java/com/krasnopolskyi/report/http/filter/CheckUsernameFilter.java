package com.krasnopolskyi.report.http.filter;

import com.krasnopolskyi.report.model.Role;
import com.krasnopolskyi.report.service.JwtService;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

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

    private String extractUsernameFromRequest(HttpServletRequest request) {
        String usernameInRequest = null;

        // If username is part of the URL
        if (request.getRequestURI().contains("/api/v1/fit-coach/report/generate")) {
            String[] parts = request.getRequestURI().split("/");
            usernameInRequest = parts[6];
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
