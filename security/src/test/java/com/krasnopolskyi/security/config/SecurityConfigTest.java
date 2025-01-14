package com.krasnopolskyi.security.config;

import com.krasnopolskyi.security.http.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {
    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private HttpSecurity httpSecurity;

    @Test
    void testAuthenticationManager() throws Exception {
        // Mocking AuthenticationConfiguration
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        AuthenticationConfiguration mockConfig = mock(AuthenticationConfiguration.class);

        when(mockConfig.getAuthenticationManager()).thenReturn(mockAuthenticationManager);

        AuthenticationManager result = securityConfig.authenticationManager(mockConfig);

        // Verify the result
        assertEquals(mockAuthenticationManager, result, "AuthenticationManager should match the mocked instance");
    }

}
