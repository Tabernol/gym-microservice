package com.krasnopolskyi.security.service;

import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.exception.AuthnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtService mockJwtService;

    @Mock
    private ProviderManager mockAuthenticationManager;

    @Mock
    private UsernamePasswordAuthenticationToken mockAuthentication;

    @Mock
    private LoginBruteForceProtectorService mockLoginProtectorService;
    @Mock
    private UserDetails mockUserDetails;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testLogOut_NoTokenInHeader() {
        // Call the logout method with an invalid authorization header
        AuthnException exception = assertThrows(AuthnException.class, () -> {
            authenticationService.logout("InvalidHeader");
        });

        // Verify behavior
        assertEquals("Token not found in request", exception.getMessage());
    }

    @Test
    void testLogOut_EmptyToken() {
        // Call the logout method with an empty authorization header
        AuthnException exception = assertThrows(AuthnException.class, () -> {
            authenticationService.logout("Bearer ");
        });

        // Verify behavior
        assertEquals("Token not found in request", exception.getMessage());
    }

    @Test
    void logout_tokenNotNull() throws AuthnException {
        String result = authenticationService.logout("Bearer notNullToken");
        assertEquals("Logged out successfully.", result);
    }

}
