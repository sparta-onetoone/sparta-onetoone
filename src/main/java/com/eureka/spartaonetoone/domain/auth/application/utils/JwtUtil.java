package com.eureka.spartaonetoone.domain.auth.application.utils;

import com.eureka.spartaonetoone.domain.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace(BEARER_PREFIX, ""))
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("토큰 만료: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 토큰: {}", e.getMessage());
        }
        return false;
    }
}