package com.eureka.spartaonetoone.order.application.dtos.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class OrderCancelRequestDto {

	@JsonProperty("order_id")
	private UUID orderId;

	@JsonProperty("customer_id")
	private UUID customerId;

	@JsonProperty("store_id")
	private UUID storeId;
}
