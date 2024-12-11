package com.krasnopolskyi.fitcoach.config.security.filter;

import com.krasnopolskyi.fitcoach.config.security.SecurityConfig;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
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
