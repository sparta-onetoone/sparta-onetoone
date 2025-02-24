package com.eureka.spartaonetoone.cart.application.dtos.response;

import java.util.List;
import java.util.UUID;

import com.eureka.spartaonetoone.cart.application.dtos.CartItemInfo;
import com.eureka.spartaonetoone.cart.domain.Cart;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartSearchResponseDto {

	@JsonProperty("cart_id")
	private UUID cartId;

	@JsonProperty("user_id")
	private UUID userId;

	@JsonProperty("cart_items")
	private List<CartItemInfo> cartItems;

	public static CartSearchResponseDto of(Cart cart, List<CartItemInfo> cartItems) {
		return CartSearchResponseDto.builder()
			.cartId(cart.getCartId())
			.userId(cart.getUserId())
			.cartItems(cartItems)
			.build();
	}
}
