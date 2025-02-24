package com.eureka.spartaonetoone.user.application.dtos.response;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.useraddress.application.dtos.response.UserAddressResponseDto;
import com.eureka.spartaonetoone.useraddress.domain.UserAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSearchResponseDto {
	private UUID userId;
	private String username;
	private String nickname;
	private String email;
	private String phoneNumber;
	private String role;
	private List<UserAddressResponseDto> addresses;  // 주소는 List로 변경

	// User 엔티티를 UserSearchResponseDto로 변환
	public static UserSearchResponseDto from(User user) {
		return UserSearchResponseDto.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.role(user.getRole().name()) // 역할을 문자열로 변환하여 저장
			.addresses(user.getAddresses().stream()
				.map(UserAddressResponseDto::from)  // UserAddressDto로 변환
				.collect(Collectors.toList()))  // List로 변환
			.build();
	}
}
