package com.anudeep.budgetmanager.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // 1. Inject values from application.properties
    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration.ms}")
    private long expirationTime;

    private Key secretKey;

    // 2. Initialize the Key object after bean creation
    /**
     * This method runs after the JwtUtil bean is constructed.
     * It decodes the Base64 secret key string from properties
     * into a secure java.security.Key object used for signing.
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Public Methods ---

    /**
     * Extracts the username (email in this case) from the JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validates the JWT.
     * Checks if the token belongs to the user and has not expired.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Generates a new JWT for a given username (email).
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        // You can add more claims to the token here if needed
        return createToken(claims, username);
    }

    // --- Private Helper Methods ---

    /**
     * A generic function to extract a specific claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * The core logic to parse the JWT and extract all its claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the token's expiration date is before the current time.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Creates the JWT string.
     * Sets the claims, subject (username), issued time, expiration time,
     * and signs it with the secret key.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}