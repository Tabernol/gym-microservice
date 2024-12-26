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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CheckUsernameFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CheckUsernameFilter checkUsernameFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldAllowAccessToExcludedPaths() throws ServletException, IOException {
        // Setup: a request to a free path (swagger resources)
        request.setRequestURI("/swagger-ui/index.html");

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: the filter chain is invoked directly
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnUnauthorizedForMissingAuthorizationHeader() throws ServletException, IOException {
        // Setup: a request without an Authorization header
        request.setRequestURI("/api/v1/fit-coach/trainees/testuser");

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: 401 Unauthorized is returned
        verify(filterChain, never()).doFilter(request, response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Token missing"));
    }

    @Test
    void shouldAllowAccessForServiceRole() throws ServletException, IOException {
        // Setup: a request with a valid token and SERVICE role
        request.setRequestURI("/api/v1/fit-coach/trainees/testuser");
        request.addHeader("Authorization", "Bearer validToken");

        // Mock JwtService to return SERVICE role
        when(jwtService.extractRoles(anyString())).thenReturn(Collections.singletonList(Role.SERVICE));

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: the filter chain is invoked
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnForbiddenIfUsernameDoesNotMatch() throws ServletException, IOException {
        // Setup: a request with valid token but mismatched usernames
        request.setRequestURI("/api/v1/fit-coach/trainees/otheruser");
        request.addHeader("Authorization", "Bearer validToken");

        // Mock JwtService to return non-SERVICE role and usernames
        when(jwtService.extractRoles(anyString())).thenReturn(Arrays.asList(Role.TRAINEE));
        when(jwtService.extractUserName(anyString())).thenReturn("authenticatedUser");

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: 403 Forbidden is returned
        verify(filterChain, never()).doFilter(request, response);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("You do not have the necessary permissions"));
    }

    @Test
    void shouldReturnForbiddenIfUsernameDoesNotMatchForTrainer() throws ServletException, IOException {
        // Setup: a request with valid token but mismatched usernames
        request.setRequestURI("/api/v1/fit-coach/trainers/otheruser");
        request.addHeader("Authorization", "Bearer validToken");

        // Mock JwtService to return non-SERVICE role and usernames
        when(jwtService.extractRoles(anyString())).thenReturn(Arrays.asList(Role.TRAINER));
        when(jwtService.extractUserName(anyString())).thenReturn("authenticatedUser");

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: 403 Forbidden is returned
        verify(filterChain, never()).doFilter(request, response);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("You do not have the necessary permissions"));
    }

    @Test
    void shouldAllowAccessIfUsernamesMatch() throws ServletException, IOException {
        // Setup: a request with valid token and matching usernames
        request.setRequestURI("/api/v1/fit-coach/trainees/testuser");
        request.addHeader("Authorization", "Bearer validToken");

        // Mock JwtService to return non-SERVICE role and matching usernames
        when(jwtService.extractRoles(anyString())).thenReturn(Arrays.asList(Role.TRAINEE));
        when(jwtService.extractUserName(anyString())).thenReturn("testuser");

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: the filter chain is invoked
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAllowAccessIfUsernamesMatchForTrainer() throws ServletException, IOException {
        // Setup: a request with valid token and matching usernames
        request.setRequestURI("/api/v1/fit-coach/trainers/testuser");
        request.addHeader("Authorization", "Bearer validToken");

        // Mock JwtService to return non-SERVICE role and matching usernames
        when(jwtService.extractRoles(anyString())).thenReturn(Arrays.asList(Role.TRAINER));
        when(jwtService.extractUserName(anyString())).thenReturn("testuser");

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: the filter chain is invoked
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleExpiredTokenException() throws ServletException, IOException {
        // Setup: a request with an invalid or expired token
        request.setRequestURI("/api/v1/fit-coach/trainees/testuser");
        request.addHeader("Authorization", "Not Bearer ");

        // Execute the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify: 401 Unauthorized is returned
        verify(filterChain, never()).doFilter(request, response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }
}
