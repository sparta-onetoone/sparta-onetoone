package com.eureka.spartaonetoone.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eureka.spartaonetoone.auth.config.JwtSecurityFilter;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtSecurityFilter jwtSecurityFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(csrf -> csrf.disable())  // CSRF 비활성화
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless 세션 관리
			.authorizeHttpRequests(auth -> auth
				// 공개 API 경로: 누구나 접근 가능
				.requestMatchers(
					"/api/v1/auth/signup",//
					"/api/v1/auth/signin",//
					"/api/v1/auth/refresh",
					"/api/v1/ai", //
					"/api/v1/addresses", //정리
					"/api/v1/stores", //정리
					"/api/v1/carts"//정리
				).permitAll()

				// Swagger UI 및 API 문서 경로를 공개
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
				.permitAll()

				// 인증된 사용자만 접근 가능한 API
				.requestMatchers("/api/v1/auth/signout").authenticated()
				.requestMatchers(HttpMethod.GET, "/api/v1/users/search").permitAll()

				// 고객, 가게 주인, 관리자 접근 가능한 API
				.requestMatchers(HttpMethod.GET, "/api/v1/users/{user_id}")
				.hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/v1/users/{user_id}")
				.hasAnyRole("CUSTOMER", "OWNER", "ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/users/{user_id}")
				.hasAnyRole("CUSTOMER", "OWNER", "ADMIN")

				// 관리자, 오너 접근 가능한 API
				.requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/products/{productId}")
				.hasAnyRole("ADMIN", "OWNER")
				.requestMatchers(HttpMethod.PUT, "/api/v1/products/{productId}")
				.hasAnyRole("ADMIN", "OWNER")
				.requestMatchers("/api/v1/products/reduce-product")
				.hasAnyRole("ADMIN", "OWNER")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/payments/{payments_id}")
				.hasAnyRole("ADMIN", "OWNER")
				.requestMatchers(HttpMethod.PUT, "/api/v1/payments/{payments_id}")
				.hasAnyRole("ADMIN", "OWNER")

				// 나머지 요청은 인증이 필요
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)  // JWT 필터 추가
			.build();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

