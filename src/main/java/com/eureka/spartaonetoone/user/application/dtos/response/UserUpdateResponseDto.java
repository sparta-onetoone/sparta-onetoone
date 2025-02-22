package com.eureka.spartaonetoone.user.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.user.domain.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateResponseDto {
	private UUID userId;
	private String username;
	private String email;

	public static UserUpdateResponseDto from(User user) {
		return UserUpdateResponseDto.builder()
			.userId(user.getUserId()) // UUID 그대로 유지
			.username(user.getUsername())
			.email(user.getEmail())
			.build();
	}
}