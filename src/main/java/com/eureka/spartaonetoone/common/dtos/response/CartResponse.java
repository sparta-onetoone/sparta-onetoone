package com.eureka.spartaonetoone.common.dtos.response;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Value;

public class CartResponse {

	@Value
	public static class Read {

		@JsonProperty("cart_id")
		UUID cartId;

		@JsonProperty("user_id")
		UUID userId;

		@JsonProperty("cart_items")
		List<CartItemInfo> cartItems;
	}

	@Getter
	public static class CartItemInfo {

		@JsonProperty("store_id")
		private UUID storeId;

		@JsonProperty("product_id")
		private UUID productId;

		@JsonProperty("product_name")
		private String productName;

		@JsonProperty("product_image")
		private String productImage;

		private int quantity;
		private int price;
	}
}
