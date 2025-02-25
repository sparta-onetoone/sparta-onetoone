package com.eureka.spartaonetoone.auth.presentation;

import org.springframework.http.ResponseEntity;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthRefreshRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSigninRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignoutRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthRefreshResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSigninResponseDto;
import com.eureka.spartaonetoone.auth.application.dtos.response.AuthSignupResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "인증 API", description = "인증 관련 API")
public interface AuthApi {

	@Operation(summary = "회원가입", description = "회원가입을 수행하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원가입 성공",
			content = @Content(schema = @Schema(implementation = AuthSignupResponseDto.class)))
	})
	ResponseEntity<CommonResponse<AuthSignupResponseDto>> signup(AuthSignupRequestDto request);

	@Operation(summary = "로그인", description = "사용자가 로그인을 수행하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = AuthSigninResponseDto.class)))
	})
	ResponseEntity<CommonResponse<AuthSigninResponseDto>> signin(AuthSigninRequestDto request);

	@Operation(summary = "로그아웃", description = "사용자가 로그아웃을 수행하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그아웃 성공",
			content = @Content(schema = @Schema(implementation = String.class)))
	})
	ResponseEntity<CommonResponse<String>> signout(String token, AuthSignoutRequestDto request);

	@Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "새로운 액세스 토큰 발급 성공",
			content = @Content(schema = @Schema(implementation = AuthRefreshResponseDto.class)))
	})
	ResponseEntity<CommonResponse<AuthRefreshResponseDto>> refreshToken(AuthRefreshRequestDto request);
}
