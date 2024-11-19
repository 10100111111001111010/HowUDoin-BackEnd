package edu.sabanciuniv.howudoin.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtWebTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        System.out.println("Secret key length: " + keyBytes.length + " bytes");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(CustomUserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userDetails.getUserId());
            claims.put("email", userDetails.getUsername());

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpiration);

            System.out.println("Generating token for user: " + userDetails.getUsername());
            System.out.println("Claims: " + claims);

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userDetails.getUserId())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                    .compact();

            System.out.println("Generated token: " + token);
            // Verify the token immediately after generation
            boolean isValid = validateToken(token);
            System.out.println("Token valid after generation: " + isValid);

            return token;
        } catch (Exception e) {
            System.out.println("Error generating token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            System.out.println("Attempting to extract user ID from token");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            System.out.println("Extracted userId: " + userId);
            return userId;
        } catch (Exception e) {
            System.out.println("Error extracting user ID from token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token: " + token);
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            System.out.println("Token validation successful");
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT validation error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to decode token without verification (for debugging)
    public void debugToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                System.out.println("Token structure:");
                System.out.println("Header: " + new String(java.util.Base64.getDecoder().decode(parts[0])));
                System.out.println("Payload: " + new String(java.util.Base64.getDecoder().decode(parts[1])));
                System.out.println("Signature present: " + !parts[2].isEmpty());
            }
        } catch (Exception e) {
            System.out.println("Error debugging token: " + e.getMessage());
        }
    }
}