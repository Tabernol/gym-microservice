package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.exception.AuthnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class LoginBruteForceProtectorServiceTest {
    @InjectMocks
    private LoginBruteForceProtectorService bruteForceProtectorService;

    private static final String USERNAME = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsBlocked_UserNotBlocked() throws AuthnException {
        bruteForceProtectorService.runBruteForceProtector(USERNAME); // First attempt

        assertDoesNotThrow(() -> bruteForceProtectorService.isBlocked(USERNAME));
    }

    @Test
    void testIsBlocked_UserBlocked() throws InterruptedException {
        bruteForceProtectorService.runBruteForceProtector(USERNAME); // 1st attempt
        bruteForceProtectorService.runBruteForceProtector(USERNAME); // 2nd attempt
        bruteForceProtectorService.runBruteForceProtector(USERNAME); // 3rd attempt

        AuthnException exception = assertThrows(AuthnException.class,
                () -> bruteForceProtectorService.isBlocked(USERNAME));
        assertTrue(exception.getMessage().contains("Please wait"));
    }
}
