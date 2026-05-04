package com.geisivan.userservice.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtUtil {

    public static final String CLAIM_EMAIL = "email";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSecretKey() {
        byte[] key = Base64.getDecoder().decode(secretKey);
        return  Keys.hmacShaKeyFor(key);
    }

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_EMAIL, email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSecretKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Optional<Claims> extractValidClaims(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();

            if (expiration == null || expiration.before(new Date())) {
                return Optional.empty();
            }
            return Optional.of(claims);

        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public Long extractUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }
}