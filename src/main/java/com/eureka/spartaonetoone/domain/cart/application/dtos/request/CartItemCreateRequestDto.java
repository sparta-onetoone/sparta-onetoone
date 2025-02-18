package com.eureka.spartaonetoone.domain.cart.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CartItemCreateRequestDto {

	@NotNull
	@JsonProperty("product_id")
	private UUID productId;

	@Min(1)
	@JsonProperty("quantity")
	private int quantity;

	public static CartItemCreateRequestDto of(UUID productId, int quantity) {
		return CartItemCreateRequestDto.builder()
			.productId(productId)
			.quantity(quantity)
			.build();
	}
}
