package com.eureka.spartaonetoone.domain.auth.config;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더가 존재하고 "Bearer "로 시작하는지 확인
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            // 토큰 유효성 검사
            if (!jwtUtil.validateToken(token)) {
                log.error("Invalid JWT token");
                throw new AuthException.InvalidTokenException();
            }

            Claims claims = jwtUtil.extractClaims(token);

            // 토큰에서 사용자 ID 추출
            String userIdStr = claims.getSubject();
            log.info("Extracted userId from token: {}", userIdStr);

            UUID userId = null;
            try {
                userId = UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format in token subject: {}", userIdStr);
                throw new AuthException.InvalidTokenException();
            }

            // 사용자 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(AuthException.UserNotFound::new);

            // UserDetailsImpl 생성
            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            // 인증 객체 생성 및 SecurityContext에 설정
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/v1/auth/signup"); // 회원가입 경로는 필터 제외
    }
}
