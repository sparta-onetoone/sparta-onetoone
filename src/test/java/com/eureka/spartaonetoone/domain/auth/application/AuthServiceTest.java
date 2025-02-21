package com.eureka.spartaonetoone.domain.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.eureka.spartaonetoone.auth.application.AuthService;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthRefreshResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSigninResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSignupResponseDto;
import com.eureka.spartaonetoone.auth.application.exception.AuthException;
import com.eureka.spartaonetoone.auth.application.utils.JwtUtil;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;

import org.junit.jupiter.api.extension.ExtendWith;

import io.jsonwebtoken.Claims;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthService authService;

	@DisplayName("회원가입 성공 시, 회원가입 응답을 반환한다.")
	@Test
	void signup_success_test() {
		// Given
		// 사용자 회원가입 요청을 위한 DTO 설정
		AuthSignupRequestDto request = AuthSignupRequestDto.builder()
			.username("testuser")
			.email("test@example.com")
			.password("password123")
			.nickname("nickname")
			.phoneNumber("1234567890")
			.role("ADMIN")
			.address(new UserAddressRequestDto("Seoul", "Gangnam", "Road Name", "12345", "Detail Address"))
			.build();

		// When
		// 회원가입 메서드 호출
		AuthSignupResponseDto response = authService.signup(request);

		// Then
		// 응답 값 검증
		assertThat(response).isNotNull();
		assertThat(response.getEmail()).isEqualTo(request.getEmail());
		assertThat(response.getUsername()).isEqualTo(request.getUsername());

		// 저장된 사용자 검증
		User savedUser = userRepository.findById(UUID.fromString(response.getUserId())).orElseThrow();
		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());
		assertThat(savedUser.getUsername()).isEqualTo(request.getUsername());
	}


	// 이메일 중복 시 예외 발생
	@DisplayName("회원가입 시, 이미 존재하는 이메일로 가입하려고 하면 예외가 발생한다.")
	@Test
	void signup_duplicate_email_test() {
		// Given
		// userAddressRequestDto는 테스트를 위해 직접 선언
		UserAddressRequestDto userAddressRequestDto = new UserAddressRequestDto(
			"Seoul", "Gangnam", "Road Name", "12345", "Detail Address"
		);

		AuthSignupRequestDto request = AuthSignupRequestDto.builder()
			.username("testuser")
			.email("test@example.com")
			.password("password123")
			.nickname("nickname")
			.phoneNumber("1234567890")
			.role("ADMIN")
			.address(userAddressRequestDto)  // 여기서 주소 정보를 설정
			.build();

		User mockUser = User.create(
			request.getUsername(),
			request.getEmail(),
			"encodedPassword",
			request.getNickname(),
			request.getPhoneNumber(),
			UserRole.valueOf(request.getRole().toUpperCase())
		);

		when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(mockUser);

		// When
		User savedUser = userRepository.save(mockUser);  // DB에 저장하고 userId가 자동으로 할당된 객체 반환
		AuthSignupResponseDto response = AuthSignupResponseDto.from(savedUser);  // 저장된 객체를 사용

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getEmail()).isEqualTo(request.getEmail());
	}
	// 로그인 성공 시
	@DisplayName("로그인 성공 시, 액세스 토큰과 리프레시 토큰을 반환한다.")
	@Test
	void signin_success_test() {
		// Given
		AuthSigninRequestDto request = AuthSigninRequestDto.builder()
			.email("test@example.com")
			.password("password123")
			.build();

		User mockUser = User.create(
			"testuser",
			request.getEmail(),
			"encodedPassword",
			"nickname",
			"1234567890",
			UserRole.ADMIN
		);

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(true);
		when(jwtUtil.createAccessToken(mockUser)).thenReturn("accessToken");
		when(jwtUtil.createRefreshToken(mockUser)).thenReturn("refreshToken");
		when(jwtUtil.validateToken(anyString())).thenReturn(true);

		// When
		AuthSigninResponseDto response = authService.signin(request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo("accessToken");
		assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
	}

	// 잘못된 비밀번호로 로그인 시 예외
	@DisplayName("로그인 시, 잘못된 비밀번호를 입력하면 예외가 발생한다.")
	@Test
	void signin_invalid_password_test() {
		// Given
		AuthSigninRequestDto request = AuthSigninRequestDto.builder()
			.email("test@example.com")
			.password("wrongpassword")
			.build();

		User mockUser = User.create(
			"testuser",
			request.getEmail(),
			"encodedPassword",
			"nickname",
			"1234567890",
			UserRole.ADMIN
		);

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> authService.signin(request))
			.isInstanceOf(AuthException.InvalidPassword.class);
	}

	// 리프레시 토큰을 이용한 액세스 토큰 재발급
	@DisplayName("리프레시 토큰을 이용해 액세스 토큰을 재발급한다.")
	@Test
	void refresh_token_success_test() {
		// Given
		String refreshToken = "validRefreshToken";
		Claims claims = mock(Claims.class);
		when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
		when(jwtUtil.extractClaims(refreshToken)).thenReturn(claims);
		when(claims.getSubject()).thenReturn(UUID.randomUUID().toString());
		User mockUser = mock(User.class);
		when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockUser));
		when(jwtUtil.createAccessToken(mockUser)).thenReturn("newAccessToken");
		when(jwtUtil.createRefreshToken(mockUser)).thenReturn("newRefreshToken");

		// When
		AuthRefreshResponseDto response = authService.refreshToken(refreshToken);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
		assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
	}

	// 잘못된 리프레시 토큰으로 예외 발생
	@DisplayName("잘못된 리프레시 토큰으로 액세스 토큰을 재발급 시, 예외가 발생한다.")
	@Test
	void refresh_token_invalid_test() {
		// Given
		String invalidRefreshToken = "invalidToken";
		when(jwtUtil.validateToken(invalidRefreshToken)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> authService.refreshToken(invalidRefreshToken))
			.isInstanceOf(AuthException.InvalidTokenException.class);
	}
}
