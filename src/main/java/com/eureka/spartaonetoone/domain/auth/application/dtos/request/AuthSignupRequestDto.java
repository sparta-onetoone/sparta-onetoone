package com.eureka.spartaonetoone.domain.auth.application.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자만 포함해야 하며, 10~11자리여야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "사용자 역할은 필수입니다.")
    private String role;

    // 주소 정보 추가
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
