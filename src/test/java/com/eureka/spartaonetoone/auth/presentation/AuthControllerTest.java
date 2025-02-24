package com.eureka.spartaonetoone.auth.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.eureka.spartaonetoone.auth.application.utils.JwtUtil;

import com.eureka.spartaonetoone.auth.application.AuthService;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignoutRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSigninResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSignupResponseDto;
import com.eureka.spartaonetoone.auth.application.exception.AuthException;
import com.eureka.spartaonetoone.auth.application.utils.JwtUtil;
import com.eureka.spartaonetoone.common.config.SecurityConfig;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsServiceImpl;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtUtil.class})
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;  // AuthService Mock 처리

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean   // SecurityConfig 의존성 해결을 위해 추가
	private UserDetailsServiceImpl userDetailsService;

	@MockitoBean
	private JwtUtil jwtUtil;  // 이 줄을 추가하세요!

	@MockitoBean
	private UserRepository userRepository;

	private final UUID testUserId = UUID.randomUUID();

	@Test
	void 회원가입_성공() throws Exception {
		// Given
		// UserAddressRequestDto를 생성하여 address에 넣어줌
		UserAddressRequestDto address = new UserAddressRequestDto(
			"서울", "강남구", "테헤란로", "123456", "역삼동 123-45"  // 상세주소 포함
		);

		// AuthSignupRequestDto에 address를 포함하여 생성
		AuthSignupRequestDto request = new AuthSignupRequestDto(
			"user1", "user1@test.com", "password123!", "nick1", "010-1111-1111", "CUSTOMER", address
		);

		// 테스트용 ID 생성
		UUID testUserId = UUID.randomUUID();
		AuthSignupResponseDto response = new AuthSignupResponseDto(
			testUserId, "user1", "user1@test.com", "nick1", "010-1111-1111", "CUSTOMER"
		);

		// Mock authService의 signup 메서드가 response를 반환하도록 설정
		when(authService.signup(any(AuthSignupRequestDto.class))).thenReturn(response);

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))) // 요청 바디
			.andExpect(status().isOk())  // 200 OK
			.andExpect(jsonPath("$.message").value("회원가입 성공"))
			.andExpect(jsonPath("$.data.userId").value(testUserId.toString()))
			.andExpect(jsonPath("$.data.username").value("user1"))
			.andExpect(jsonPath("$.data.email").value("user1@test.com"))
			.andExpect(jsonPath("$.data.nickname").value("nick1"))
			.andExpect(jsonPath("$.data.phoneNumber").value("010-1111-1111"))
			.andExpect(jsonPath("$.data.role").value("CUSTOMER"));

		// Mock이 제대로 호출되었는지 검증
		verify(authService, times(1)).signup(any(AuthSignupRequestDto.class));
	}

	@Test
	void 로그인_성공() throws Exception {
		// Given
		AuthSigninRequestDto request = new AuthSigninRequestDto(
			"user1@test.com", "password123!" // 이메일과 비밀번호 입력
		);

		String mockAccessToken = "mockAccessToken";
		String mockRefreshToken = "mockRefreshToken";

		// 로그인 성공 시 반환될 응답 설정
		AuthSigninResponseDto response = AuthSigninResponseDto.of(mockAccessToken, mockRefreshToken);

		// Mock authService의 signin 메서드가 response를 반환하도록 설정
		when(authService.signin(any(AuthSigninRequestDto.class))).thenReturn(response);

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))) // 요청 바디
			.andExpect(status().isOk())  // 200 OK
			.andExpect(jsonPath("$.message").value("로그인 성공"))
			.andExpect(jsonPath("$.data.accessToken").value(mockAccessToken))
			.andExpect(jsonPath("$.data.refreshToken").value(mockRefreshToken));

		// Mock이 제대로 호출되었는지 검증
		verify(authService, times(1)).signin(any(AuthSigninRequestDto.class));
	}

}