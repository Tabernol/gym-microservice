//package com.krasnopolskyi.fitcoach.config.security.filter;
//
//import com.krasnopolskyi.fitcoach.service.JwtService;
//import com.krasnopolskyi.fitcoach.service.UserService;
//import io.jsonwebtoken.JwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.lang.NonNull;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserService userService;
//
//    private static final List<String> FREE_PATHS = Arrays.asList(
//            "/swagger-ui/**",
//            "/v3/api-docs/**",
//            "/swagger-resources/**",
//            "/api/v1/fit-coach/training-types" // Allow this end-point for creating Front-end part
//    );
//
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String requestPath = request.getRequestURI();
//        log.info("req path " + requestPath);
//
//        // Bypass JWT filter for excluded paths
//        if (isExcludedPath(requestPath)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String authHeader = request.getHeader("Authorization");
//        final String token;
//        final String userEmail;
//
//        // missing token
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            handleExpiredTokenException(response, "Token missing", HttpStatus.UNAUTHORIZED);
//            return;
//        }
//
//        // checking token
//        token = authHeader.substring(7);
//        try {
//
//            // Validate the token
//            if (jwtService.isTokenValid(token)) {
//                // Add token validation logic or user authentication setting here if needed
//            } else {
//                log.info("Token invalid");
//                handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
//                return;
//            }
//            // Continue the request if token is valid
//            filterChain.doFilter(request, response);
//
//        } catch (JwtException e) {
//            log.error("JWT token is invalid. ", e);
//            handleExpiredTokenException(response, "JWT token is expired or invalid", HttpStatus.UNAUTHORIZED); // Handle the exception
//        } catch (AuthenticationException e) {
//            log.error("Authentication failed: ", e);
//            handleExpiredTokenException(response, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED); // Handle the exception
//        }
//    }
//
//    public static boolean isExcludedPath(String requestPath) {
//        AntPathMatcher pathMatcher = new AntPathMatcher();
//        return FREE_PATHS.stream().anyMatch(path -> pathMatcher.match(path, requestPath));
//    }
//
//    private void handleExpiredTokenException(HttpServletResponse response, String message, HttpStatus status) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//        response.getWriter().write("{ \"status\": " + status.value() + "," +
//                "\"message\": \"" + message + "\"}");
//    }
//}
