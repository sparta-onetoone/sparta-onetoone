package com.eureka.spartaonetoone.domain.auth.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eureka.spartaonetoone.common.config.SecurityConfig;
import com.eureka.spartaonetoone.domain.auth.application.AuthService;
import com.eureka.spartaonetoone.domain.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.response.AuthSignupResponseDto;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.request.UserAddressRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = AuthController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
	}
)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AuthService authService;

	@DisplayName("회원가입을 요청한다.")
	@Test
	void signup_request_test() throws Exception {
		// Given
		UserAddressRequestDto address = new UserAddressRequestDto(
			"Seoul", "Gangnam", "Some Road", "12345", "Some Detail"
		);

		AuthSignupRequestDto request = AuthSignupRequestDto.builder()
			.email("test@example.com")
			.password("password123")
			.username("testuser")
			.nickname("testuser")
			.phoneNumber("01041714141")
			.role("ADMIN")
			.address(address)
			.build();

		AuthSignupResponseDto responseDto = AuthSignupResponseDto.builder()
			.userId(UUID.randomUUID().toString())
			.username("testuser")
			.email("test@example.com")
			.nickname("testuser")
			.phoneNumber("01041714141")
			.role("ADMIN")
			.build();

		// When: Mock authService.signup 호출 시 responseDto 반환
		when(authService.signup(any(AuthSignupRequestDto.class))).thenReturn(responseDto);

		// Then: MockMvc를 사용하여 회원가입 요청을 보내고, 응답 값을 검증
		mockMvc.perform(post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S000"))  // 응답 코드 확인
			.andExpect(jsonPath("$.data.userId").value(responseDto.getUserId()))  // userId 필드 확인
			.andExpect(jsonPath("$.message").value("회원가입 성공"));  // 메시지 확인

		// Verify: authService.signup 메서드가 호출되었는지 검증
		verify(authService).signup(any(AuthSignupRequestDto.class));
	}
}
