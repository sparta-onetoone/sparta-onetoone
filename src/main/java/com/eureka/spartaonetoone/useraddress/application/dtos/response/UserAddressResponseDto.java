package com.eureka.spartaonetoone.useraddress.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.useraddress.domain.UserAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressResponseDto {
	private UUID addressId;
	private String city;
	private String district;
	private String roadName;
	private String zipCode;
	private String detail;

	public static UserAddressResponseDto from(UserAddress address) {
		return UserAddressResponseDto.builder()
			.addressId(address.getAddressId())
			.city(address.getCity())
			.district(address.getDistrict())
			.roadName(address.getRoadName())
			.zipCode(address.getZipCode())
			.detail(address.getDetail())
			.build();
	}
}
