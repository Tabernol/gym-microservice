package com.krasnopolskyi.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String OLD_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJUUkFJTkVSIl0sInN1YiI6InVzYWluLmJvbHQiLCJpYXQiOjE3MzUxMTcxMTYsImV4cCI6MTczNTExNzcxNn0.Tc7Xxsf4CIz2mpZDRMMguo5HC3yz316opGvttSUdepA";
    @InjectMocks
    private JwtService jwtService;

    @Mock
    private Claims claims;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", "SW1hZ2luYXRpb24gaXMgbW9yZSBpbXBvcnRhbnQgdGhhbiBrbm93bGVkZ2Uu");
    }

    @Test
    public void IsTokenValid_ThrowsException() {
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(OLD_TOKEN));
    }

    @Test
    public void addTokenToBlackListTest(){
        assertTrue(jwtService.addToBlackList(OLD_TOKEN));
    }

    @Test
    public void IsTokenValid_FalseTest() {
        String testToken = "test";
        jwtService.addToBlackList(testToken);
        assertFalse(jwtService.isTokenValid(testToken));
    }

}
