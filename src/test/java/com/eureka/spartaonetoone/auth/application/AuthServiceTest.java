package com.eureka.spartaonetoone.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eureka.spartaonetoone.auth.application.AuthService;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSigninResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSignupResponseDto;
import com.eureka.spartaonetoone.auth.application.exception.AuthException;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;

@SpringBootTest
class AuthServiceTest {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@DisplayName("회원가입 시, 유효한 입력으로 회원이 생성된다.")
	@Test
	void signup_test() {
		// Given
		UserAddressRequestDto addressDto = new UserAddressRequestDto("Seoul", "Gangnam", "Street", "12345", "Apt 101");
		AuthSignupRequestDto requestDto = new AuthSignupRequestDto(
			"testuser",
			"testuser@example.com",
			"Password@123",
			"TestUser",
			"010-1234-5678",
			"CUSTOMER",
			addressDto
		);

		// When
		AuthSignupResponseDto response = authService.signup(requestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getUsername()).isEqualTo(requestDto.getUsername());
		assertThat(response.getEmail()).isEqualTo(requestDto.getEmail());
		assertThat(response.getRole()).isEqualTo(requestDto.getRole());

		// Verify the user is saved in the repository
		User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(() -> new AuthException.UserNotFound());
		assertThat(user.getUsername()).isEqualTo(requestDto.getUsername());
		assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
	}

	@DisplayName("이메일로 로그인 시, 유효한 토큰이 반환된다.")
	@Test
	void signin_test() {
		// Given
		String encodedPassword = passwordEncoder.encode("Password@123");
		User user = User.create("testuser", "testuser@example.com", encodedPassword, "TestUser", "010-1234-5678", UserRole.CUSTOMER);
		userRepository.save(user);

		AuthSigninRequestDto requestDto = new AuthSigninRequestDto("testuser@example.com", "Password@123");

		// When
		AuthSigninResponseDto response = authService.signin(requestDto);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isNotBlank();
		assertThat(response.getRefreshToken()).isNotBlank();
	}

	@DisplayName("로그인 시 잘못된 비밀번호 입력 시, 예외가 발생한다.")
	@Test
	void signin_invalid_password_test() {
		// Given
		String encodedPassword = passwordEncoder.encode("Password@123");
		User user = User.create("testuser", "testuser@example.com", encodedPassword, "TestUser", "010-1234-5678", UserRole.CUSTOMER);
		userRepository.save(user);

		AuthSigninRequestDto requestDto = new AuthSigninRequestDto("testuser@example.com", "WrongPassword");

		// When & Then
		assertThatThrownBy(() -> authService.signin(requestDto))
			.isInstanceOf(AuthException.InvalidPassword.class);
	}

	@DisplayName("로그인 시 존재하지 않는 이메일 입력 시, 예외가 발생한다.")
	@Test
	void signin_user_not_found_test() {
		// Given
		AuthSigninRequestDto requestDto = new AuthSigninRequestDto("nonexistent@example.com", "Password@123");

		// When & Then
		assertThatThrownBy(() -> authService.signin(requestDto))
			.isInstanceOf(AuthException.UserNotFound.class);
	}
}
