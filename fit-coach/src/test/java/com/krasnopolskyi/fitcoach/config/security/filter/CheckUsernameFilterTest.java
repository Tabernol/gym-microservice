package com.krasnopolskyi.fitcoach.config.security.filter;

import com.krasnopolskyi.fitcoach.config.security.SecurityConfig;
import com.krasnopolskyi.fitcoach.config.security.filter.CheckUsernameFilter;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
class CheckUsernameFilterTest {
    @InjectMocks
    private CheckUsernameFilter checkUsernameFilter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext(); // Clear context to avoid test interference
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }


    @Test
    void testDoFilterInternal_ExcludedPath() throws ServletException, IOException {
        // Mock an excluded path
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/fit-coach/trainees/create");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock SecurityConfig.isExcludedPath() to return true
        mockStatic(SecurityConfig.class);
        when(SecurityConfig.isExcludedPath(request.getRequestURI())).thenReturn(true);

        // Call the filter
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Verify the filter chain was called without blocking
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldSetForbidden_whenAuthenticatedUsernameDoesNotMatch() throws ServletException, IOException {
        // Arrange
        request.setRequestURI("/api/v1/fit-coach/trainees/testUser/update");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("authenticatedUser"); // Authenticated user is different
        SecurityContextHolder.setContext(securityContext);

        // Act
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus()); // Status should be 403
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"status\": 403,\"message\": \"You do not have the necessary permissions to access this resource.\"}",
                response.getContentAsString());
        verify(filterChain, never()).doFilter(request, response); // Request should NOT proceed
    }

    @Test
    void doFilterInternal_shouldAllowRequest_whenAuthenticatedUsernameMatches() throws ServletException, IOException {
        // Arrange
        request.setRequestURI("/api/v1/fit-coach/trainees/testUser/update");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser"); // Authenticated user matches the request username
        SecurityContextHolder.setContext(securityContext);

        // Act
        checkUsernameFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response); // Request should proceed
        assertEquals(HttpStatus.OK.value(), response.getStatus()); // No errors, request should go through
    }

//    @Test
//    void doFilterInternal_shouldSetForbidden_whenNoAuthenticatedUser() throws ServletException, IOException {
//        // Arrange
//        request.setRequestURI("/api/v1/fit-coach/trainees/testUser/update");
//        when(securityContext.getAuthentication()).thenReturn(null); // No authenticated user
//        SecurityContextHolder.setContext(securityContext);
//
//        // Act
//        checkUsernameFilter.doFilterInternal(request, response, filterChain);
//
//        // Assert
//        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
//        assertEquals("application/json", response.getContentType());
//        assertEquals("{ \"status\": 403,\"message\": \"You do not have the necessary permissions to access this resource.\"}",
//                response.getContentAsString());
//        verify(filterChain, never()).doFilter(request, response); // Request should NOT proceed
//    }
}
