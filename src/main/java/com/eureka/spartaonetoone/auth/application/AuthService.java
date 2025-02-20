package com.eureka.spartaonetoone.auth.application;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.eureka.spartaonetoone.useraddress.domain.UserAddress;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil; // jwtUtil 선언 추가

	@Transactional
	public AuthSignupResponseDto signup(AuthSignupRequestDto request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new AuthException.DuplicateEmail();
		}

		// User 생성
		User user = User.create(
			request.getUsername(),
			request.getEmail(),
			passwordEncoder.encode(request.getPassword()),
			request.getNickname(),
			request.getPhoneNumber(),
			UserRole.valueOf(request.getRole().toUpperCase())
		);

		// 회원가입 요청에서 주소 정보 가져오기
		UserAddressRequestDto addressDto = request.getAddress();
		UserAddress userAddress = UserAddress.create(
			addressDto.getCity(),
			addressDto.getDistrict(),
			addressDto.getRoadName(),
			addressDto.getZipCode(),
			addressDto.getDetail()
		);

		user.setDefaultAddress(userAddress);
		userRepository.save(user);

		return AuthSignupResponseDto.from(user);
	}

	@Transactional
	public AuthSigninResponseDto signin(AuthSigninRequestDto request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(AuthException.UserNotFound::new);

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new AuthException.InvalidPassword();
		}

		String accessToken = jwtUtil.createAccessToken(user);
		String refreshToken = jwtUtil.createRefreshToken(user);
		user.updateRefreshToken(refreshToken);

		return AuthSigninResponseDto.of(accessToken, refreshToken);
	}

	@Transactional
	public void signout(String email, String token) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(AuthException.UserNotFound::new);

		String jwtToken = token.replace("Bearer ", "");
		if (!jwtUtil.validateToken(jwtToken)) {
			throw new AuthException.InvalidTokenException();
		}

		user.updateRefreshToken(null);
	}

	// 리프레시 토큰으로 액세스 토큰 재발급
	@Transactional
	public AuthRefreshResponseDto refreshToken(String refreshToken) {
		// 리프레시 토큰 검증
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new AuthException.InvalidTokenException();
		}

		// Claims에서 사용자 정보 추출
		Claims claims = jwtUtil.extractClaims(refreshToken);
		String userIdStr = claims.getSubject();
		UUID userId;
		try {
			userId = UUID.fromString(userIdStr);
		} catch (IllegalArgumentException e) {
			throw new AuthException.InvalidTokenException();
		}

		// 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new AuthException.UserNotFound());

		// 새로운 액세스 토큰 발급
		String newAccessToken = jwtUtil.createAccessToken(user);

		// 새로운 리프레시 토큰 발급 (필요 시)
		String newRefreshToken = jwtUtil.createRefreshToken(user);
		user.updateRefreshToken(newRefreshToken);

		return AuthRefreshResponseDto.of(newAccessToken, newRefreshToken);
	}
}
