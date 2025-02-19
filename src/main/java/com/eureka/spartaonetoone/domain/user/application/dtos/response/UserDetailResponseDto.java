package com.eureka.spartaonetoone.domain.user.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.domain.user.domain.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponseDto {

	private UUID userId;
	private String username;
	private String nickname;
	private String email;

	// private 생성자
	private UserDetailResponseDto(UUID userId, String username, String nickname, String email) {
		this.userId = userId;
		this.username = username;
		this.nickname = nickname;
		this.email = email;
	}

	// 빌더 패턴을 위한 내부 클래스
	public static class Builder {
		private UUID userId;
		private String username;
		private String nickname;
		private String email;

		public Builder userId(UUID userId) {
			this.userId = userId;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder nickname(String nickname) {
			this.nickname = nickname;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public UserDetailResponseDto build() {
			return new UserDetailResponseDto(userId, username, nickname, email);
		}
	}

	// User 객체에서 정보를 받아서 UserDetailResponseDto로 변환
	public static UserDetailResponseDto from(User user) {
		return new Builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.build();
	}

	// getter
	public UUID getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getNickname() {
		return nickname;
	}

	public String getEmail() {
		return email;
	}

	// setter (필요하다면 setter를 추가할 수 있습니다)
	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
