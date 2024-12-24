package com.krasnopolskyi.security.http.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.krasnopolskyi.security.dto.UserCredentials;
import com.krasnopolskyi.security.exception.AuthnException;
import com.krasnopolskyi.security.exception.EntityException;
import com.krasnopolskyi.security.service.AuthenticationService;
import com.krasnopolskyi.security.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthnControllerTest {

    @InjectMocks
    private AuthnController authnController;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Mock
    private AuthenticationService authenticationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws EntityException, AuthnException {
        // Arrange
        UserCredentials credentials = new UserCredentials("username", "password");
        String token = "valid_token";
        when(authenticationService.logIn(any())).thenReturn(token);

        // Act
        ResponseEntity<String> response = authnController.login(credentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }



    @Test
    void logout_ShouldReturnSuccessMessage_WhenLogoutIsSuccessful() throws EntityException, AuthnException {
        // Arrange
        String token = "valid_token";
        when(authenticationService.logout(anyString())).thenReturn("Logged out successfully.");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        // Act
        ResponseEntity<String> response = authnController.logout(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully.", response.getBody());

        // Verify that logout was called
        verify(authenticationService, times(1)).logout("Bearer " + token);
    }

    @Test
    void logout_ShouldThrowAuthnException_WhenTokenIsInvalid() throws EntityException, AuthnException {
        // Arrange
        String token = "invalid_token";
        when(authenticationService.logout(anyString())).thenThrow(new AuthnException("Invalid token"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        // Act & Assert
        Exception exception = assertThrows(AuthnException.class, () -> authnController.logout(request));
        assertEquals("Invalid token", exception.getMessage());
    }



}
