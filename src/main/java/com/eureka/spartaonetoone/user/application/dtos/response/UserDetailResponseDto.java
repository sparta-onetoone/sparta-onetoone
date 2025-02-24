package com.eureka.spartaonetoone.user.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDetailResponseDto {

	private UUID userId;
	private String username;
	private String nickname;
	private String email;
	private String phoneNumber;

	public static UserDetailResponseDto from(User user) {
		return UserDetailResponseDto.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.build();
	}
}