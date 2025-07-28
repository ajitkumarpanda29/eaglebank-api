package com.bclay.eaglebank_api.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final long EXPIRATION_MS = 86400000; // 1 day
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username) {
        logger.debug("Generating JWT token for user: {}", username);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
        logger.debug("Token generated successfully for user: {}", username);
        return token;
    }

    public String extractUsername(String token) {
        logger.debug("Extracting username from token.");
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            logger.debug("Username extracted: {}", username);
            return username;
        } catch (JwtException e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        logger.debug("Validating token.");
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            logger.debug("Token is valid.");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }
}
