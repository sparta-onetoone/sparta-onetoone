package com.eureka.spartaonetoone.user.presentation;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDeleteResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserListResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserUpdateResponseDto;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserRepository userRepository;

	@DisplayName("유저 정보를 조회한다.")
	@Test
	void testGetUserDetail() {
		// Given
		User user = userRepository.save(User.create("Test User", "test@example.com", "password", "Test", "010-1234-5678", UserRole.ADMIN));
		UUID userId = user.getUserId();

		// When
		ResponseEntity<UserDetailResponseDto> response = restTemplate.getForEntity("/api/v1/users/{user_id}", UserDetailResponseDto.class, userId);

		// Then
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getUserId()).isEqualTo(userId.toString());
		assertThat(response.getBody().getUsername()).isEqualTo("Test User");
	}

	@DisplayName("모든 유저 정보를 조회한다.")
	@Test
	void testGetAllUsers() {
		// Given
		User user = userRepository.save(User.create("Test User", "test@example.com", "password", "Test", "010-1234-5678", UserRole.ADMIN));

		// When
		ResponseEntity<UserListResponseDto[]> response = restTemplate.getForEntity("/api/v1/users", UserListResponseDto[].class);

		// Then
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
	}

	@DisplayName("유저 정보를 수정한다.")
	@Test
	void testUpdateUser() {
		// Given
		User user = userRepository.save(User.create("Test User", "test@example.com", "password", "Test", "010-1234-5678", UserRole.ADMIN));
		UUID userId = user.getUserId();
		UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("Updated User", "updated@example.com");

		// When
		ResponseEntity<UserUpdateResponseDto> response = restTemplate.exchange(
			"/api/v1/users/{user_id}", HttpMethod.PUT, new HttpEntity<>(updateRequest), UserUpdateResponseDto.class, userId);

		// Then
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getUserId()).isEqualTo(userId.toString());
		assertThat(response.getBody().getUsername()).isEqualTo("Updated User");
		assertThat(response.getBody().getEmail()).isEqualTo("updated@example.com");
	}

	@DisplayName("유저를 삭제한다.")
	@Test
	void testDeleteUser() {
		// Given
		User user = userRepository.save(User.create("Test User", "test@example.com", "password", "Test", "010-1234-5678", UserRole.ADMIN));
		UUID userId = user.getUserId();

		// When
		ResponseEntity<UserDeleteResponseDto> response = restTemplate.exchange(
			"/api/v1/users/{user_id}", HttpMethod.DELETE, null, UserDeleteResponseDto.class, userId);

		// Then
		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getUserId()).isEqualTo(userId.toString());
	}

}
