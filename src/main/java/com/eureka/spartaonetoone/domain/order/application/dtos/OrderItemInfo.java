package com.eureka.spartaonetoone.domain.order.application.dtos;

import java.util.UUID;

import com.eureka.spartaonetoone.domain.order.domain.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OrderItemInfo {

	@JsonProperty("order_item_id")
	private UUID orderItemId;

	@JsonProperty("product_id")
	private UUID productId;

	@JsonProperty("product_name")
	private String productName;

	@JsonProperty("product_image")
	private String productImage;

	private int quantity;
	private int price;

	public static OrderItemInfo from(OrderItem orderItem) {
		return OrderItemInfo.builder()
			.orderItemId(orderItem.getOrderItemId())
			.productId(orderItem.getProductId())
			.productName(orderItem.getProductName())
			.productImage(orderItem.getProductImage())
			.quantity(orderItem.getQuantity())
			.price(orderItem.getPrice())
			.build();
	}
}
