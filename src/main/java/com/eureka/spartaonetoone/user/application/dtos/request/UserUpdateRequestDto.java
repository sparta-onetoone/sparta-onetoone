package com.eureka.spartaonetoone.user.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

	@NotBlank(message = "사용자 이름은 필수입니다.")
	private String username;

	@NotBlank(message = "이메일은 필수입니다.")
	private String email;
}