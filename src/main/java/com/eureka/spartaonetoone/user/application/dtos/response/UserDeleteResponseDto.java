package com.eureka.spartaonetoone.user.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.user.domain.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDeleteResponseDto {
	private UUID userId;

	public static UserDeleteResponseDto from(User user) {
		return UserDeleteResponseDto.builder()
			.userId(user.getUserId()) // UUID 그대로 유지
			.build();
	}
}
