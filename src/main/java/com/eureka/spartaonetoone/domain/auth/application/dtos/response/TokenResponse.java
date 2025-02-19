package com.eureka.spartaonetoone.domain.auth.application.dtos.response;

import lombok.Getter;

@Getter
public class TokenResponse {

	private String accessToken;
	private String refreshToken;

	// private 생성자 (빌더 패턴을 위한 생성자)
	private TokenResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	// Getter
	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	// 빌더 클래스
	public static class Builder {
		private String accessToken;
		private String refreshToken;

		// 액세스 토큰 세터
		public Builder accessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		// 리프레시 토큰 세터
		public Builder refreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
			return this;
		}

		// 빌더 메소드로 TokenResponse 객체 생성
		public TokenResponse build() {
			return new TokenResponse(accessToken, refreshToken);
		}
	}

	// 빌더 메소드 (간편하게 빌더를 사용할 수 있도록 추가)
	public static Builder builder() {
		return new Builder();
	}
}