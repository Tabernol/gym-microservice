package com.krasnopolskyi.fitcoach.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class provides functionality for generate and validate JWT token
 *
 */
@Service
public class JwtService {
    private final Set<String> tokenBlackList = new HashSet<>(); // todo next step to do it using Redis
    //unique key for generating token
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    //extract Username from header
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return generateToken(claims, userDetails.getUsername());
    }


    public boolean isTokenValid(String token, String username) {
        if(tokenBlackList.contains(token)){
            return false;
        }
        final String extractedUserName = extractUserName(token);
        return (extractedUserName.equals(username)) && !isTokenExpired(token);
    }

    public void addToBlackList(String token){
        tokenBlackList.add(token);
    }

    //return different claims from token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis())) //setting date of granting token
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) // the token is valid for 10 minutes
                .signWith(getSigningKey())
                .compact();
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

    // Generate key from jwtSigningKey in application.jaml
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Scheduled(fixedRate = 60000)  // Clean the blacklist every 60 seconds
    public void cleanUpBlacklist() {
        tokenBlackList.removeIf(this::isTokenExpired);
    }
}
