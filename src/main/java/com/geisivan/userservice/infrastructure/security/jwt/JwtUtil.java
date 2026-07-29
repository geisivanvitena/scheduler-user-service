package com.geisivan.userservice.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
public class JwtUtil {

    public static final String CLAIM_EMAIL = "email";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private final Clock clock;

    public JwtUtil(Clock clock) {
        this.clock = clock;
    }

    private SecretKey getSecretKey() {
        byte[] key = Base64.getDecoder().decode(secretKey);
        return  Keys.hmacShaKeyFor(key);
    }

    public String generateToken(Long userId, String email) {
        Instant now = clock.instant();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_EMAIL, email)
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(expiry))
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
            return Optional.of(extractClaims(token));

        } catch (JwtException | IllegalArgumentException exception) {

            log.debug("JWT validation failed | reason: {}",
                    exception.getClass().getSimpleName());

            return Optional.empty();
        }
    }

    public Optional<Long> extractUserId(Claims claims) {
        String subject = claims.getSubject();

        if (subject == null || subject.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(Long.valueOf(subject));
    }
}
