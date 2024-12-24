package com.krasnopolskyi.fitcoach.service;

import com.krasnopolskyi.fitcoach.entity.Role;
import io.jsonwebtoken.Claims;
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
    void generateTokenTest(){
        String token = jwtService.generateServiceToken();
        assertNotNull(token);
    }
    @Test
    void isTokenValidTest(){
        String token = jwtService.generateServiceToken();
        assertTrue(jwtService.isTokenValid(token));
    }
    @Test
    void extractRoleTest(){
        String token = jwtService.generateServiceToken();
        List<Role> roles = jwtService.extractRoles(token);
        assertEquals(Role.SERVICE, roles.get(0));
    }

    @Test
    void extractUsernameTest(){
        String token = jwtService.generateServiceToken();
        String userName = jwtService.extractUserName(token);
        assertEquals("fit-coach-service", userName);
    }

}
