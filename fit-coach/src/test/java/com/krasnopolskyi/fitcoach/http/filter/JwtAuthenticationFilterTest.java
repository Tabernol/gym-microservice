package com.krasnopolskyi.fitcoach.http.filter;

import com.krasnopolskyi.fitcoach.entity.Role;
import com.krasnopolskyi.fitcoach.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testDoFilterInternal_ExcludedPath() throws ServletException, IOException {
        // Set an excluded path (e.g., Swagger path)
        request.setRequestURI("/swagger-ui/index.html");

        // Call the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify the filter chain is called, meaning the request was bypassed
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDoFilterInternal_MissingAuthorizationHeader() throws ServletException, IOException {
        // Simulate a request without Authorization header
        request.setRequestURI("/api/v1/fit-coach/trainees");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Expect 401 Unauthorized due to missing token
        assertEquals(401, response.getStatus());
        assertEquals("{ \"status\": 401,\"message\": \"Token missing\"}", response.getContentAsString());
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Set an invalid Authorization header
        request.addHeader("Authorization", "Bearer invalid_token");
        request.setRequestURI("/api/v1/fit-coach/trainees");

        // Mock JwtService to return false for invalid token
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Expect 401 Unauthorized due to invalid token
        assertEquals(401, response.getStatus());
        assertEquals("{ \"status\": 401,\"message\": \"JWT token is expired or invalid\"}", response.getContentAsString());
    }

    @Test
    public void testDoFilterInternal_ValidToken_AuthorizedPath() throws ServletException, IOException {
        // Set a valid Authorization header
        String token = "valid_token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRequestURI("/api/v1/fit-coach/trainees");

        // Mock JwtService to return true for valid token
        when(jwtService.isTokenValid(token)).thenReturn(true);

        // Mock the roles that the user has
        List<Role> roles = Arrays.asList(Role.TRAINEE);
        when(jwtService.extractRoles(token)).thenReturn(roles);

        // Call the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the filter chain continues after setting the authentication
        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testDoFilterInternal_ValidToken_UnauthorizedPath() throws ServletException, IOException {
        // Set a valid Authorization header
        String token = "valid_token";
        request.addHeader("Authorization", "Bearer " + token);
        request.setRequestURI("/api/v1/fit-coach/trainees/create");

        // Mock JwtService to return true for valid token
        when(jwtService.isTokenValid(token)).thenReturn(true);

        // Mock roles (Trainee role cannot access "create" endpoints)
        List<Role> roles = Arrays.asList(Role.TRAINEE);
        when(jwtService.extractRoles(token)).thenReturn(roles);

        // Call the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Expect 403 Forbidden due to unauthorized access
//        assertEquals(403, response.getStatus());
        assertEquals("{ \"status\": 403,\"message\": \"Access denied\"}", response.getContentAsString());
    }
}
