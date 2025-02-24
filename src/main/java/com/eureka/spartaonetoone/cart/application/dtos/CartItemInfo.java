package com.eureka.spartaonetoone.cart.application.dtos;

import java.util.UUID;

import com.eureka.spartaonetoone.cart.domain.CartItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CartItemInfo {

	@JsonProperty("cart_item_id")
	private UUID cartItemId;

	@JsonProperty("store_id")
	private UUID storeId;

	@JsonProperty("product_id")
	private UUID productId;

	@JsonProperty("product_name")
	private String productName;

	@JsonProperty("product_image")
	private String productImage;

	@JsonProperty("quantity")
	private int quantity;

	@JsonProperty("price")
	private int price;

	public static CartItemInfo from(CartItem cartItem) {
		return CartItemInfo.builder()
			.cartItemId(cartItem.getCartItemId())
			.storeId(cartItem.getStoreId())
			.productId(cartItem.getProductId())
			.productName(cartItem.getProductName())
			.productImage(cartItem.getProductImage())
			.quantity(cartItem.getQuantity())
			.price(cartItem.getPrice())
			.build();
	}
}
