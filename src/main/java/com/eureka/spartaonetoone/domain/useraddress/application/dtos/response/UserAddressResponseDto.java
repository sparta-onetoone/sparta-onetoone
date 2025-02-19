package com.eureka.spartaonetoone.domain.useraddress.application.dtos.response;

import com.eureka.spartaonetoone.domain.useraddress.domain.UserAddress;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAddressResponseDto {

    private String addressId;
    private String city;
    private String district;
    private String loadName;
    private String zipCode;
    private String detail;

    public static UserAddressResponseDto from(UserAddress address) {
        return UserAddressResponseDto.builder()
                .addressId(address.getAddressId().toString())
                .city(address.getCity())
                .district(address.getDistrict())
                .loadName(address.getRoadName())
                .zipCode(address.getZipCode())
                .detail(address.getDetail())
                .build();
    }
}
