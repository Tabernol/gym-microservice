package com.krasnopolskyi.security.http.filter;

import com.krasnopolskyi.security.service.JwtService;
import com.krasnopolskyi.security.service.UserService;
import com.krasnopolskyi.security.service.UserServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternal_MissingAuthorizationHeader() throws ServletException, IOException {
        // Simulate a request with no Authorization header
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Expect the response to be 401 UNAUTHORIZED
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("{ \"status\": 401,\"message\": \"Token missing\"}", response.getContentAsString());
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Set an invalid Authorization header
        request.addHeader("Authorization", "Bearer invalid_token");

        // Mock JwtService to throw JwtException when an invalid token is passed
        when(jwtService.extractUserName(anyString())).thenThrow(new JwtException("Invalid JWT"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Expect the response to be 401 UNAUTHORIZED with an error message
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("{ \"status\": 401,\"message\": \"JWT token is expired or invalid\"}", response.getContentAsString());
    }
}
