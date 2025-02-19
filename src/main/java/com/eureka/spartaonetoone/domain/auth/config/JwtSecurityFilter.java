package com.eureka.spartaonetoone.domain.auth.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eureka.spartaonetoone.domain.auth.application.exception.AuthException;
import com.eureka.spartaonetoone.domain.auth.application.utils.JwtUtil;
import com.eureka.spartaonetoone.domain.user.domain.User;
import com.eureka.spartaonetoone.domain.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.domain.user.infrastructure.security.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

	private static final String[] EXCLUDED_PATHS = {
		"/api/v1/auth/signup",
		"/api/v1/auth/signin",
		"/api/v1/auth/refresh"
	};

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain) throws ServletException, IOException {

		// Authorization 헤더에서 토큰 추출
		String authorization = request.getHeader("Authorization");

		if (authorization != null && authorization.startsWith("Bearer ")) {
			String token = authorization.substring(7);

			// 유효성 검사
			if (!jwtUtil.validateToken(token)) {
				throw new AuthException.InvalidTokenException();
			}

			// Claims 추출
			Claims claims = jwtUtil.extractClaims(token);

			String userIdStr = claims.getSubject();
			log.info("Extracted userId from token: {}", userIdStr);

			UUID userId;
			try {
				userId = UUID.fromString(userIdStr);
			} catch (IllegalArgumentException e) {
				log.error("Invalid UUID format in token subject: {}", userIdStr);
				throw new AuthException.InvalidTokenException();
			}

			// User 조회
			User user = userRepository.findById(userId)
				.orElseThrow(AuthException.UserNotFound::new);

			// UserDetailsImpl 생성: fromUser 메서드를 사용하여 UserDetailsImpl 객체 생성
			UserDetailsImpl userDetails = UserDetailsImpl.fromUser(user);
			// 인증 객체 생성
			JwtAuthenticationToken authentication = new JwtAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
			);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		for (String excludedPath : EXCLUDED_PATHS) {
			if (path.equals(excludedPath)) {
				return true;  // 필터링 제외 대상이면 true 반환
			}
		}
		return false;
	}
}
