package com.eureka.spartaonetoone.useraddress.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserAddressSearchRequestDto {
	private String city;
	private String district;
	private String roadName;
	private String zipCode;
	@JsonProperty("user_id")  // User ID를 매핑할 때 사용
	private UUID userId;
}

