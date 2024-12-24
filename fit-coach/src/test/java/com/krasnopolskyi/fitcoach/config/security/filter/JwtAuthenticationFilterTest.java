package com.krasnopolskyi.fitcoach.config.security.filter;

import com.krasnopolskyi.fitcoach.http.filter.JwtAuthenticationFilter;
import com.krasnopolskyi.fitcoach.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(jwtAuthenticationFilter).build();
    }

//    @Test
//    @Disabled
//    void doFilterInternal_whenValidToken_shouldAuthenticate() throws ServletException, IOException {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setRequestURI("/api/v1/fit-coach/secure");
//        request.addHeader("Authorization", "Bearer valid-jwt-token");
//        MockHttpServletResponse response = new MockHttpServletResponse();
//
//        String username = "testUser";
//        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                username, "password", Collections.emptyList());
//
//        when(jwtService.extractUserName("valid-jwt-token")).thenReturn(username);
////        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
//        when(jwtService.isTokenValid("valid-jwt-token", username)).thenReturn(true);
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        // Verify the filter chain is continued
//        verify(filterChain).doFilter(request, response);
//
//        // Check that authentication is set
//        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be set for valid token");
//    }
//
//    @Test
//    void doFilterInternal_whenTokenExpired_shouldReturnUnauthorized() throws ServletException, IOException {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setRequestURI("/api/v1/fit-coach/secure");
//        request.addHeader("Authorization", "Bearer expired-jwt-token");
//        MockHttpServletResponse response = new MockHttpServletResponse();
//
//        when(jwtService.extractUserName("expired-jwt-token")).thenThrow(new ExpiredJwtException(null, null, "JWT expired"));
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        // Check that the response has the correct status for expired token
//        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus(), "Should return 401 Unauthorized for expired token");
//    }
}
