package com.eureka.spartaonetoone.auth.application.dtos.request;

import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthSignupRequestDto {

	@NotBlank(message = "사용자 이름은 필수입니다.")
	private String username;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "이메일 형식이 맞지 않습니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/`~]).{8,}$",
		message = "비밀번호는 최소 8자 이상, 알파벳, 숫자, 특수문자가 포함되어야 합니다.")
	private String password;

	@NotBlank(message = "닉네임은 필수입니다.")
	private String nickname;

	@NotBlank(message = "전화번호는 필수입니다.")
	private String phoneNumber;

	@NotBlank(message = "사용자 역할은 필수입니다.")
	private String role;

	private UserAddressRequestDto address;


}