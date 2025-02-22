package com.eureka.spartaonetoone.user.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.user.domain.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponseDto {

	private UUID userId;
	private String username;
	private String nickname;
	private String email;
	private String phoneNumber;
	// User 객체에서 정보를 받아서 UserDetailResponseDto로 변환
	public static UserDetailResponseDto from(User user) {
		return UserDetailResponseDto.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.build();
	}
}