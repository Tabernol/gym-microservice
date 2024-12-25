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

//    @Test
//    void logIn_ShouldReturnToken_WhenCredentialsAreValid() throws AuthnException {
//        // Arrange
//        UserCredentials userCredentials = new UserCredentials("user1", "password1");
//        Authentication authentication = mock(Authentication.class);
//        UserDetails userDetails = mock(UserDetails.class);
//        String expectedToken = "mockToken";
//
//        when(authentication.getName()).thenReturn("user1");
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(jwtService.generateToken(userDetails)).thenReturn(expectedToken);
//
//        // Act
//        String token = authenticationService.logIn(userCredentials);
//
//        // Assert
//        assertEquals(expectedToken, token);
//        verify(loginProtectorService, times(1)).isBlocked(userCredentials.username());
//        verify(jwtService, times(1)).generateToken(userDetails);
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//    }
//
//    @Test
//    void logIn_ShouldThrowAuthnException_WhenCredentialsAreInvalid() throws AuthnException {
//        // Arrange
//        UserCredentials userCredentials = new UserCredentials("user1", "wrongPassword");
//
//        when(mockAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuthentication);
////                .thenThrow(new BadCredentialsException("Invalid credentials"));
//        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);
//        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
//
//        assertNotNull(mockAuthentication);
//
//        // Act & Assert
//        AuthnException exception = assertThrows(AuthnException.class, () -> {
//            authenticationService.logIn(userCredentials);
//        });
//
//        assertEquals("Invalid credentials", exception.getMessage());
//        assertEquals(401, exception.getCode());
//        verify(mockLoginProtectorService, times(1)).runBruteForceProtector(userCredentials.username());
//    }
//
//    @Test
//    void logIn_ShouldBlockUser_WhenUserIsBlocked() throws AuthnException {
//        // Arrange
//        UserCredentials userCredentials = new UserCredentials("user1", "password1");
//
//        doThrow(new AuthnException("User is blocked")).when(loginProtectorService).isBlocked(userCredentials.username());
//
//        // Act & Assert
//        AuthnException exception = assertThrows(AuthnException.class, () -> {
//            authenticationService.logIn(userCredentials);
//        });
//
//        assertEquals("User is blocked", exception.getMessage());
//        verify(loginProtectorService, times(1)).isBlocked(userCredentials.username());
//        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
//    }

}
