package com.eureka.spartaonetoone.useraddress.application.dtos.response;

import com.eureka.spartaonetoone.useraddress.domain.UserAddress;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAddressResponseDto {
	private String addressId;
	private String city;
	private String district;
	private String roadName;
	private String zipCode;
	private String detail;

	public static UserAddressResponseDto from(UserAddress address) {
		return UserAddressResponseDto.builder()
			.addressId(address.getAddressId().toString())
			.city(address.getCity())
			.district(address.getDistrict())
			.roadName(address.getRoadName())
			.zipCode(address.getZipCode())
			.detail(address.getDetail())
			.build();
	}
}
