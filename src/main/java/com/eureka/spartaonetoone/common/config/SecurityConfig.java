package com.eureka.spartaonetoone.common.config;

import com.eureka.spartaonetoone.domain.auth.config.JwtSecurityFilter;
import com.eureka.spartaonetoone.domain.user.infrastructure.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 회원가입과 로그인은 누구나 접근 가능
                        .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/signin", "/error").permitAll()
                        // 로그아웃은 인증된 사용자만 가능
                        .requestMatchers("/api/v1/auth/signout").authenticated()
                        // 회원 상세 조회는 '고객', '가게 주인', '관리자'만 가능
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{user_id}").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        // 회원 전체 조회는 '관리자'만 가능
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                        // 회원 수정은 '고객', '가게 주인'만 가능
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/{user_id}").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        // 회원 탈퇴는 '고객', '가게 주인', '관리자'만 가능
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{user_id}").hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}