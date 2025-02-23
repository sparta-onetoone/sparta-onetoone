package com.eureka.spartaonetoone.auth.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.auth.application.AuthService;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthRefreshRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignoutRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthRefreshResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSigninResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSignupResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	//회원가입
	@PostMapping("/signup")
	public ResponseEntity<CommonResponse<AuthSignupResponseDto>> signup(
		@Valid @RequestBody AuthSignupRequestDto request) {

		return ResponseEntity.ok(CommonResponse.success(authService.signup(request), "회원가입 성공"));
	}

	//로그인
	@PostMapping("/signin")
	public ResponseEntity<CommonResponse<AuthSigninResponseDto>> signin(
		@Valid @RequestBody AuthSigninRequestDto request) {

		return ResponseEntity.ok(CommonResponse.success(authService.signin(request), "로그인 성공"));
	}

	//로그아웃
	@PostMapping("/signout")
	public ResponseEntity<CommonResponse<String>> signout(
		@RequestHeader("Authorization") String token,
		@RequestBody AuthSignoutRequestDto request) {

		authService.signout(request.getEmail(), token);
		return ResponseEntity.ok(CommonResponse.success("로그아웃 성공", "로그아웃 성공"));
	}

	// 리프레시 토큰을 이용한 토큰 재발급
	@PostMapping("/refresh")
	public ResponseEntity<CommonResponse<AuthRefreshResponseDto>> refreshToken(
		@RequestBody AuthRefreshRequestDto request) {

		// 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
		AuthRefreshResponseDto response = authService.refreshToken(request.getRefreshToken());
		return ResponseEntity.ok(CommonResponse.success(response, "새로운 액세스 토큰 발급 성공"));
	}
}