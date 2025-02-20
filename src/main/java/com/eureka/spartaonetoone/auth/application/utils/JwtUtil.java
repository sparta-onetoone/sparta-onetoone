package com.eureka.spartaonetoone.auth.application.utils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eureka.spartaonetoone.user.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

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

	// Access Token 생성
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

	// Refresh Token 생성
	public String createRefreshToken(User user) {
		Date now = new Date();
		return Jwts.builder()
			.setSubject(user.getUserId().toString())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + refreshTokenExpirationTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// Claims 추출
	public Claims extractClaims(String token) {
		return Jwts.parser()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token.replace(BEARER_PREFIX, ""))
			.getBody();
	}

	// 토큰 유효성 검사
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

	// 토큰에서 이메일 정보 추출
	public String getUserEmailFromToken(String token) {
		Claims claims = extractClaims(token); // Claims 추출
		return claims.get("email", String.class); // 이메일 정보 추출
	}

	// 리프레시 토큰을 사용하여 새로운 액세스 토큰 생성
	public String createAccessTokenFromRefreshToken(String refreshToken) {
		// 리프레시 토큰에서 Claims 추출
		Claims claims = extractClaims(refreshToken);

		// 사용자 ID와 이메일 추출
		String userId = claims.getSubject();
		String userEmail = claims.get("email", String.class);

		// 새로운 액세스 토큰 생성
		return Jwts.builder()
			.setSubject(userId)
			.claim("email", userEmail)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}
}
