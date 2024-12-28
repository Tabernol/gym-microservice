package com.krasnopolskyi.report.service;

import com.krasnopolskyi.report.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class provides functionality for generate and validate JWT token
 *
 */
@Service
public class JwtService {
    //unique key for generating token
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<Role> extractRoles(String token) {
        // Extract roles from JWT claims (depending on how you encode roles in the token)
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class); // Assuming roles are stored as a list in the JWT
        return roles.stream().map(Role::valueOf).collect(Collectors.toList());
    }

    public String generateServiceToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("SERVICE"));
        return generateToken(claims, "report-service");
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    //return different claims from token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    //check token on Date
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis())) //setting date of granting token
                .expiration(new Date(System.currentTimeMillis() + 1000 * 20)) // the token is valid for 20 seconds
                .signWith(getSigningKey())
                .compact();
    }

    // Generate key from jwtSigningKey in application.jaml
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
