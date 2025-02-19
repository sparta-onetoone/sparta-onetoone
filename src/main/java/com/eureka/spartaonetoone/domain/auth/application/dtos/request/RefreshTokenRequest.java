package com.eureka.spartaonetoone.domain.auth.application.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
	private String refreshToken;  // 클라이언트가 보내는 리프레시 토큰
}
