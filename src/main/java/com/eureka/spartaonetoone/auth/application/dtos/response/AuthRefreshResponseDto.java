package com.eureka.spartaonetoone.auth.application.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRefreshResponseDto {
	private String accessToken;
	private String refreshToken;

	// 팩토리 메서드로 DTO 생성
	public static AuthRefreshResponseDto of(String accessToken, String refreshToken) {
		return AuthRefreshResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}