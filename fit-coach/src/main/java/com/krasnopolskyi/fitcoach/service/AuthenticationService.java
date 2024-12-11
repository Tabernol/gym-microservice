package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.dto.request.UserCredentials;
import com.krasnopolskyi.fitcoach.exception.AuthnException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginBruteForceProtectorService loginProtectorService;

    public String logIn(UserCredentials userCredentials) throws AuthnException {
        loginProtectorService.isBlocked(userCredentials.username());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCredentials.username(), userCredentials.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return jwtService.generateToken(userDetails);

        } catch (BadCredentialsException e) {
            loginProtectorService.runBruteForceProtector(userCredentials.username());
            AuthnException authnException = new AuthnException("Invalid credentials");
            authnException.setCode(HttpStatus.UNAUTHORIZED.value());
            throw authnException;
        }
    }

    public String logout(String authorizationHeader) throws AuthnException {
        String token = extractToken(authorizationHeader);
        if (token != null && !token.isEmpty()) {
            jwtService.addToBlackList(token);
            return "Logged out successfully.";
        } else {
            AuthnException authnException = new AuthnException("Token not found in request");
            authnException.setCode(HttpStatus.UNAUTHORIZED.value());
            throw authnException;
        }
    }

    // Helper method to extract token from Authorization header
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract JWT token without "Bearer "
        }
        return null;
    }
}
