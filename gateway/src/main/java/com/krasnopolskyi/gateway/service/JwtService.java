package com.krasnopolskyi.gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

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
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public boolean isTokenValid(String token) {
        if(tokenBlackList.contains(token)){
            return false;
        }
        return !isTokenExpired(token);
    }

    public void addToBlackList(String token){
        tokenBlackList.add(token);
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
