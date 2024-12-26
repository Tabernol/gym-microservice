package com.krasnopolskyi.report.http.filter;

import com.krasnopolskyi.report.model.Role;
import com.krasnopolskyi.report.service.JwtService;
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
    public void testDoFilterInternal_MissingAuthorizationHeader() throws ServletException, IOException {
        // Simulate a request without Authorization header
        request.setRequestURI("/api/v1/report/generate");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the response is 401 Unauthorized due to missing token
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("{ \"status\": 401,\"message\": \"Token missing\"}", response.getContentAsString());
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Simulate a request with an invalid token
        request.addHeader("Authorization", "Bearer invalid_token");
        request.setRequestURI("/api/v1/report/generate");

        // Mock JwtService to return false for invalid token
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the response is 401 Unauthorized due to invalid token
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("{ \"status\": 401,\"message\": \"JWT token is expired or invalid\"}", response.getContentAsString());
    }

    @Test
    public void testDoFilterInternal_ValidToken_AuthorizedRole_Service() throws ServletException, IOException {
        // Simulate a request with a valid token and SERVICE role
        request.addHeader("Authorization", "Bearer valid_token");
        request.setRequestURI("/api/v1/fit-coach/report/training-session");

        // Mock JwtService to return true for valid token
        when(jwtService.isTokenValid("valid_token")).thenReturn(true);

        // Mock JwtService to return SERVICE role
        List<Role> roles = Arrays.asList(Role.SERVICE);
        when(jwtService.extractRoles("valid_token")).thenReturn(roles);

        // Call the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the filter chain is called and the request is forwarded
        verify(filterChain).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testDoFilterInternal_ValidToken_AuthorizedRole_Trainer() throws ServletException, IOException {
        // Simulate a request with a valid token and TRAINER role
        request.addHeader("Authorization", "Bearer valid_token");
        request.setRequestURI("/api/v1/fit-coach/report/generate/123");

        // Mock JwtService to return true for valid token
        when(jwtService.isTokenValid("valid_token")).thenReturn(true);

        // Mock JwtService to return TRAINER role
        List<Role> roles = Arrays.asList(Role.TRAINER);
        when(jwtService.extractRoles("valid_token")).thenReturn(roles);

        // Call the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the filter chain is called and the request is forwarded
        verify(filterChain).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testDoFilterInternal_ValidToken_UnauthorizedRole() throws ServletException, IOException {
        // Simulate a request with a valid token but with an unauthorized role
        request.addHeader("Authorization", "Bearer valid_token");
        request.setRequestURI("/api/v1/report/generate");

        // Mock JwtService to return true for valid token
        when(jwtService.isTokenValid("valid_token")).thenReturn(true);

        // Mock JwtService to return an unauthorized role (e.g., TRAINEE)
        List<Role> roles = Arrays.asList(Role.SERVICE);
        when(jwtService.extractRoles("valid_token")).thenReturn(roles);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the response is 403 Forbidden due to unauthorized access
//        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertEquals("{ \"status\": 403,\"message\": \"Access denied\"}", response.getContentAsString());
    }

    // Utility to simulate unauthorized access for specific role (unauthorized role)
    @Test
    public void testDoFilterInternal_ValidToken_InvalidPath() throws ServletException, IOException {
        // Simulate a request with valid token and valid roles, but invalid path
        request.addHeader("Authorization", "Bearer valid_token");
        request.setRequestURI("/api/v1/report/invalid-path");

        // Mock JwtService to return true for valid token
        when(jwtService.isTokenValid("valid_token")).thenReturn(true);

        // Mock JwtService to return valid roles (but the path doesn't match any authorized roles)
        List<Role> roles = Arrays.asList(Role.TRAINER);
        when(jwtService.extractRoles("valid_token")).thenReturn(roles);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that the response is 403 Forbidden due to access denied
//        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertEquals("{ \"status\": 403,\"message\": \"Access denied\"}", response.getContentAsString());
    }
}
