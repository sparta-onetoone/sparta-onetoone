package com.eureka.spartaonetoone.order.application.dtos.request;

import java.util.UUID;

import com.eureka.spartaonetoone.order.domain.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCreateRequestDto {

	@NotNull
	@JsonProperty("cart_id")
	private UUID cartId;

	@NotNull
	@JsonProperty("type")
	private OrderType type;

	@JsonProperty("requests")
	@Size(max = 50)
	private String requests;
}
