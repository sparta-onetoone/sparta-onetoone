package com.eureka.spartaonetoone.domain.useraddress.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserAddressRequestDto {

	@NotBlank(message = "시/도는 필수 입력 값입니다.")
	private String city;

	@NotBlank(message = "군/구는 필수 입력 값입니다.")
	private String district;

	@NotBlank(message = "도로명은 필수 입력 값입니다.")
	private String roadName;

	@NotBlank(message = "우편번호는 필수 입력 값입니다.")
	private String zipCode;

	private String detail; // 상세주소 (선택)

}