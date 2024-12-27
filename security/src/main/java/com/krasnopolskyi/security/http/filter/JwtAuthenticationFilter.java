package com.krasnopolskyi.security.http.filter;

import com.krasnopolskyi.security.config.SecurityConfig;
import com.krasnopolskyi.security.entity.Role;
import com.krasnopolskyi.security.service.JwtService;
import com.krasnopolskyi.security.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j(topic = "SECURITY-JWT")
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Bypass JWT filter for excluded paths
        if (SecurityConfig.isExcludedPath(requestPath)) {
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
            userEmail = jwtService.extractUserName(token);
            List<Role> roles = jwtService.extractRoles(token);

            // allow access for SERVICE role
            if (roles.contains(Role.SERVICE)) {
                // Set authentication in SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail, null, Collections.singleton(Role.SERVICE));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request, response);
                return; // I don't see log from controller here
            }

            // check token and authenticated user
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                } else {
                    log.error("JWT token is invalid. ");
                    handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
                    return;
                }
            }
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            log.error("JWT token is invalid. ", e);
            handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED); // Handle the exception
        } catch (AuthenticationException e) {
            log.error("Authentication failed: ", e);
            handleExpiredTokenException(response, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED); // Handle the exception
        }
    }

    private void handleExpiredTokenException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{ \"status\": " + status.value() + "," +
                "\"message\": \"" + message + "\"}");
    }
}
