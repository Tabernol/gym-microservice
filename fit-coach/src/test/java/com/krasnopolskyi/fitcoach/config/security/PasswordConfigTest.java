package com.krasnopolskyi.fitcoach.config.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {
    @Test
    void passwordEncoderBean_shouldReturnBCryptPasswordEncoder() {
        // Create a Spring context with the PasswordConfig class
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PasswordConfig.class);

        // Get the PasswordEncoder bean
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);

        // Check that the PasswordEncoder bean is an instance of BCryptPasswordEncoder
        assertNotNull(passwordEncoder, "PasswordEncoder bean should not be null");
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder, "PasswordEncoder should be an instance of BCryptPasswordEncoder");

        context.close();
    }

    @Test
    void passwordEncoder_shouldEncodeAndMatchPasswords() {
        // Create a new instance of the configuration
        PasswordConfig config = new PasswordConfig();
        PasswordEncoder passwordEncoder = config.passwordEncoder();

        // Test encoding a password
        String rawPassword = "mySecurePassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Assert that the raw password is not equal to the encoded password
        assertNotEquals(rawPassword, encodedPassword, "Encoded password should not match raw password");

        // Verify that the encoded password matches the raw password
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "PasswordEncoder should correctly match the raw and encoded password");
    }
}
