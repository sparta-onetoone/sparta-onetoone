package com.eureka.spartaonetoone.domain.auth.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.auth.application.AuthService;
import com.eureka.spartaonetoone.domain.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.response.AuthSigninResponseDto;
import com.eureka.spartaonetoone.domain.auth.application.dtos.response.AuthSignupResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<CommonResponse<AuthSignupResponseDto>> signup(
            @Valid @RequestBody AuthSignupRequestDto request) {

        return ResponseEntity.ok(CommonResponse.success(authService.signup(request), "회원가입 성공"));
    }
    //로그인
    @PostMapping("/api/v1/auth/signin")
    public ResponseEntity<CommonResponse<AuthSigninResponseDto>> signin(
            @Valid @RequestBody AuthSigninRequestDto request) {

        return ResponseEntity.ok(CommonResponse.success(authService.signin(request), "로그인 성공"));
    }
    // 로그아웃
    @PostMapping("/api/v1/auth/signout")
    public ResponseEntity<CommonResponse<String>> signout(
            @RequestHeader("Authorization") String token) {

        authService.signout(token);

        return ResponseEntity.ok(CommonResponse.success("로그아웃 성공", "로그아웃 성공"));
    }
}