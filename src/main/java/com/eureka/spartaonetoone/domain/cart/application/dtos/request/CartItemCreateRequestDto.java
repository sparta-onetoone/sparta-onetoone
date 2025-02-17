package com.eureka.spartaonetoone.domain.cart.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CartItemCreateRequestDto {

	@NotNull
	@JsonProperty("product_id")
	private UUID productId;

	@Min(1)
	@JsonProperty("quantity")
	private int quantity;
}
