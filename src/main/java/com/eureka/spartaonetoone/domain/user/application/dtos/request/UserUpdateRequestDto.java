package com.eureka.spartaonetoone.domain.user.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {

	@NotBlank(message = "사용자 이름은 필수입니다.")
	private String username;

	@NotBlank(message = "이메일은 필수입니다.")
	private String email;
}