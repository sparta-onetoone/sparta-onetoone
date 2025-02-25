package com.eureka.spartaonetoone.order.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderSearchRequestDto {

	@JsonProperty("store_id")
	private UUID storeId;

	@JsonProperty("user_id")
	private UUID userId;
}
