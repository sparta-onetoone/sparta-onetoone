package com.eureka.spartaonetoone.order.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderRequestDto {

	@NotNull
	@JsonProperty("order_id")
	private UUID orderId;
}
