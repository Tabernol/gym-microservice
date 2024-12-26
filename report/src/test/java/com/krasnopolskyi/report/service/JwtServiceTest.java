package com.krasnopolskyi.report.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String testJwtSigningKey = "SW1hZ2luYXRpb24gaXMgbW9yZSBpbXBvcnRhbnQgdGhhbiBrbm93bGVkZ2Uu";

    private static final String OLD_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJUUkFJTkVSIl0sInN1YiI6InVzYWluLmJvbHQiLCJpYXQiOjE3MzUxMTcxMTYsImV4cCI6MTczNTExNzcxNn0.Tc7Xxsf4CIz2mpZDRMMguo5HC3yz316opGvttSUdepA";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the signing key via reflection
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", testJwtSigningKey);
    }

    @Test
    public void IsTokenValid_ThrowsException() {
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(OLD_TOKEN));
    }
}
