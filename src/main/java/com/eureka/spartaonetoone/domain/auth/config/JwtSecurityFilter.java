package com.eureka.spartaonetoone.domain.auth.config;

import com.eureka.spartaonetoone.domain.auth.application.utils.JwtUtil;
import com.eureka.spartaonetoone.domain.user.domain.User;
import com.eureka.spartaonetoone.domain.user.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
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

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.extractClaims(token);
                User user = userRepository.findById(UUID.fromString(claims.getSubject()))
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // JWT 토큰에서 정보를 사용하여 인증 처리
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(user.getRole().getAuthority())
                );

                JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                        user.getUserId().toString(),
                        null,
                        authorities // 권한 전달
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
