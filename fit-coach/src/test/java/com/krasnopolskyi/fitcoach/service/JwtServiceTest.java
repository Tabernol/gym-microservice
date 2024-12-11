package com.krasnopolskyi.fitcoach.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;
    @Mock
    private UserDetails userDetails;

    // Sample key for testing (ensure it's valid Base64)
    private final String testJwtSigningKey = "SW1hZ2luYXRpb24gaXMgbW9yZSBpbXBvcnRhbnQgdGhhbiBrbm93bGVkZ2Uu";

    private String testUsername = "testUser";

    private final String TOKEN = "validJwtToken";
    private String generatedToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject the signing key via reflection
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", testJwtSigningKey);
        Mockito.when(userDetails.getUsername()).thenReturn(testUsername);
        // Generate a test token for the test cases
        generatedToken = jwtService.generateToken(userDetails);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(generatedToken, "Token should be generated");
        assertTrue(generatedToken.startsWith("eyJ"), "Token should start with 'eyJ'");
    }

    @Test
    void testExtractUserName() {
        String extractedUsername = jwtService.extractUserName(generatedToken);
        assertEquals(testUsername, extractedUsername, "Extracted username should match");
    }

    @Test
    void testAddToBlackList() {
        // Call the addToBlackList method
        jwtService.addToBlackList(TOKEN);

        // Verify that the token is now in the blacklist
        Set<String> tokenBlackList = getTokenBlackList();
        assertTrue(tokenBlackList.contains(TOKEN));
    }

    // Helper method to access the private tokenBlackList
    private Set<String> getTokenBlackList() {
        try {
            var field = JwtService.class.getDeclaredField("tokenBlackList");
            field.setAccessible(true);
            return (Set<String>) field.get(jwtService);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIsTokenValid_TokenInBlackList() {
        // Add token to the blacklist
        jwtService.addToBlackList(TOKEN);

        // Call the isTokenValid method
        boolean isValid = jwtService.isTokenValid(TOKEN, testUsername);

        // Verify that the token is invalid because it is in the blacklist
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_TokenExpired() {
        String token = jwtService.generateToken(userDetails);

        // Verify behavior and result
        assertTrue(jwtService.isTokenValid(token, testUsername));  // Token is expired
    }
}
