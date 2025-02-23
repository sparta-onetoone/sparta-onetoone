package com.eureka.spartaonetoone.auth.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthSignupResponseDto {
	private UUID userId;  // UUID -> String 변환된 값
	private String username;
	private String email;
	private String nickname;
	private String phoneNumber;
	private String role; // CUSTOMER, ADMIN, OWNER 등 역할 반환

	// 빌더 패턴을 사용하여 DTO 생성
	public static AuthSignupResponseDto from(User user) {
		return AuthSignupResponseDto.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.phoneNumber(user.getPhoneNumber())
			.role(user.getRole().name())
			.build();
	}
}
