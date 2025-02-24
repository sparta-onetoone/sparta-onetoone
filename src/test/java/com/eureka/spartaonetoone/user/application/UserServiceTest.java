package com.eureka.spartaonetoone.user.application;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDeleteResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserListResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserUpdateResponseDto;
import com.eureka.spartaonetoone.user.application.exception.UserException;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;


@SpringBootTest
class UserServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	// 유저 생성 후 상세 조회 테스트
	@DisplayName("회원 상세 조회 시, 정상적으로 사용자 정보가 반환된다.")
	@Test
	void getUserDetail_test() {
		// Given
		User user = User.create("testuser", "testuser@example.com", "Password@123", "TestUser", "010-1234-5678",
			UserRole.CUSTOMER);
		userRepository.save(user);

		// When
		UserDetailResponseDto response = userService.getUserDetail(user.getUserId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getUsername()).isEqualTo(user.getUsername());
		assertThat(response.getEmail()).isEqualTo(user.getEmail());
	}

	@DisplayName("삭제된 사용자 접근 시 예외가 발생한다.")
	@Test
	void getUserDetail_deleted_user_test() {
		// Given
		// 삭제된 사용자 생성
		User user = User.create("testuser", "testuser@example.com", "encodedPassword", "TestUser", "010-1234-5678",
			UserRole.CUSTOMER);
		user.markAsDeleted(UUID.randomUUID()); // 사용자 삭제 처리
		userRepository.save(user);

		// When & Then
		assertThatThrownBy(() -> userService.getUserDetail(user.getUserId()))
			.isInstanceOf(UserException.DeletedUserAccessException.class); // 삭제된 사용자 예외 발생 확인
	}

	// 사용자 목록 조회 테스트 (페이지네이션)
	@DisplayName("회원 전체 조회 시, 페이지네이션으로 사용자 목록이 반환된다.")
	@Test
	void getAllUsers_test() {
		// Given
		User user1 = User.create("testuser1", "testuser1@example.com", "Password@123", "TestUser1", "010-1234-5678",
			UserRole.CUSTOMER);
		User user2 = User.create("testuser2", "testuser2@example.com", "Password@123", "TestUser2", "010-1234-5678",
			UserRole.CUSTOMER);
		userRepository.save(user1);
		userRepository.save(user2);

		// When
		Page<UserListResponseDto> response = userService.getAllUsers(PageRequest.of(0, 10));

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getTotalElements()).isEqualTo(2);
	}

	// 사용자 정보 수정 테스트
	@DisplayName("회원 수정 시, 정상적으로 수정된 사용자 정보가 반환된다.")
	@Test
	void updateUser_test() {
		// Given
		User user = User.create("testuser", "testuser@example.com", "Password@123", "TestUser", "010-1234-5678",
			UserRole.CUSTOMER);
		userRepository.save(user);

		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("updatedUser", "updatedEmail@example.com");

		// When
		UserUpdateResponseDto response = userService.updateUser(user.getUserId(), requestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getUsername()).isEqualTo(requestDto.getUsername());
		assertThat(response.getEmail()).isEqualTo(requestDto.getEmail());
	}

	// 존재하지 않는 사용자 수정 시 예외 발생 테스트
	@DisplayName("존재하지 않는 사용자 수정 시, 예외가 발생한다.")
	@Test
	void updateUser_not_found_test() {
		// Given
		UUID nonExistentUserId = UUID.randomUUID();
		UserUpdateRequestDto requestDto = new UserUpdateRequestDto("updatedUser", "updatedEmail@example.com");

		// When & Then
		assertThatThrownBy(() -> userService.updateUser(nonExistentUserId, requestDto))
			.isInstanceOf(UserException.UserNotFoundException.class);
	}
	//회원탈퇴
	@DisplayName("회원 탈퇴 시, 정상적으로 탈퇴된 사용자 정보가 반환된다.")
	@Test
	void deleteUser_test() {
		// Given
		User user = User.create("testuser", "testuser@example.com", "Password@123", "TestUser", "010-1234-5678",
			UserRole.CUSTOMER);
		userRepository.save(user);

		// When
		UserDeleteResponseDto response = userService.deleteUser(user.getUserId());

		// Then
		assertThat(response).isNotNull();
		// UUID 객체를 직접 비교합니다.
		assertThat(response.getUserId()).isEqualTo(user.getUserId());  // UUID 비교
	}
	// 존재하지 않는 사용자 탈퇴 시 예외 발생 테스트
	@DisplayName("존재하지 않는 사용자 탈퇴 시, 예외가 발생한다.")
	@Test
	void deleteUser_not_found_test() {
		// Given
		UUID nonExistentUserId = UUID.randomUUID();

		// When & Then
		assertThatThrownBy(() -> userService.deleteUser(nonExistentUserId))
			.isInstanceOf(UserException.UserNotFoundException.class);
	}
}