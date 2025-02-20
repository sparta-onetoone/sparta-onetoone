package com.eureka.spartaonetoone.auth.application.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRefreshRequestDto {
	private String refreshToken;
}
