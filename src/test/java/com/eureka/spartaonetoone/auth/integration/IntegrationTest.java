package com.eureka.spartaonetoone.auth.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // 테스트 후 롤백
class IntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	private String accessToken;
	private UUID testUserId;
	private UUID testAddressId; // 주소 ID를 저장할 변수

	// 초기화
	// 각 테스트 메서드가 실행되기 전에 실행되는 메서드
	//여기서는 테스트 사용자 생성, 로그인 후 액세스 토큰을 발급받고, 저장하는 작업
	@BeforeEach
	void setUp() throws Exception {
		// 1. 테스트 사용자 생성
		AuthSignupRequestDto signupRequest = new AuthSignupRequestDto(
			"testuser",
			"test@example.com",
			"Password@123",
			"Test User",
			"010-1234-5678",
			"ADMIN",
			new UserAddressRequestDto("Seoul", "Gangnam", "Teheran-ro", "12345", "101")
		);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequest)))
			.andExpect(status().isOk());

		// 2. 로그인으로 토큰 발급
		AuthSigninRequestDto loginRequest = new AuthSigninRequestDto(
			"test@example.com",
			"Password@123"
		);

		String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();

		this.accessToken = extractToken(response); // 토큰 파싱 메서드 필요
		this.testUserId = userRepository.findByEmail("test@example.com")
			.orElseThrow().getUserId();
	}

	// 토큰 추출 도우미 메서드
	private String extractToken(String jsonResponse) {
		// 실제 구현에서는 JSON Path 라이브러리 사용 권장
		return jsonResponse.split("\"accessToken\":\"")[1].split("\"")[0];
	}

	@Test
	@DisplayName("전체 사용자 흐름 테스트: 회원가입 → 주소 추가 → 조회 → 수정 → 삭제")
	void fullUserFlowTest() throws Exception {
		// 1. 주소 추가
		UserAddressRequestDto addressRequest = new UserAddressRequestDto(
			"Seoul", "Gangnam", "Teheran-ro 123", "12345", "Apt 101"
		);

		// 주소 추가 후 주소 ID 추출
		String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/addresses/{user_id}", testUserId)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(addressRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("주소 추가 성공"))
			.andReturn().getResponse().getContentAsString();

		// 주소 ID 추출 (실제 구현에서는 응답 JSON에서 ID를 파싱)
		this.testAddressId = UUID.fromString(response.split("\"addressId\":\"")[1].split("\"")[0]);

		// 2. 사용자 상세 조회
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{user_id}", testUserId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.email").value("test@example.com"));

		// 3. 주소 수정
		UserAddressRequestDto updateRequest = new UserAddressRequestDto(
			"Busan", "Haeundae", "Centum Road", "54321", "Apt 202"
		);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/addresses/{address_id}", testAddressId)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("주소 수정 성공"));

		// 4. 회원 탈퇴
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{user_id}", testUserId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("회원 탈퇴 성공"));

		// 5. 삭제된 사용자 접근 시도 (삭제된 사용자에 대해 404 Not Found 반환)
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{user_id}", testUserId)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNotFound())  // 여기서 상태 코드를 404로 설정
			.andExpect(jsonPath("$.message").value("삭제된 사용자에 접근할 수 없습니다.")); // 예외 메시지 확인
	}
}
