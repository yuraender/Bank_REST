package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiry-time}")
    private int expiryTime;

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("pwd", user.getPassword())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Timestamp.from(Instant.now().plus(expiryTime, ChronoUnit.SECONDS)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
