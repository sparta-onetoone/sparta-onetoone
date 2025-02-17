package com.eureka.spartaonetoone.domain.cart.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.domain.cart.domain.Cart;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartCreateResponseDto {

	@JsonProperty("cart_id")
	private UUID cartId;

	public static CartCreateResponseDto from(Cart cart) {
		return CartCreateResponseDto.builder()
			.cartId(cart.getCartId())
			.build();
	}
}
