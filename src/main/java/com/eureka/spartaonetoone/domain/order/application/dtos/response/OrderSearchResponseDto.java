package com.eureka.spartaonetoone.domain.order.application.dtos.response;

import java.util.List;
import java.util.UUID;

import com.eureka.spartaonetoone.domain.order.application.dtos.OrderItemInfo;
import com.eureka.spartaonetoone.domain.order.domain.Order;
import com.eureka.spartaonetoone.domain.order.domain.OrderStatus;
import com.eureka.spartaonetoone.domain.order.domain.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OrderSearchResponseDto {

	@JsonProperty("order_id")
	private UUID orderId;

	@JsonProperty("user_id")
	private UUID userId;

	@JsonProperty("store_id")
	private UUID storeId;

	private OrderStatus status;

	private OrderType type;

	private String requests;

	@JsonProperty("total_price")
	private int totalPrice;

	@JsonProperty("order_items")
	private List<OrderItemInfo> orderItems;

	public static OrderSearchResponseDto from(Order order) {
		return OrderSearchResponseDto.builder()
			.orderId(order.getOrderId())
			.userId(order.getUserId())
			.storeId(order.getStoreId())
			.status(order.getStatus())
			.type(order.getType())
			.requests(order.getRequests())
			.totalPrice(order.getTotalPrice())
			.orderItems(order.getOrderItems().stream().map(OrderItemInfo::from).toList())
			.build();
	}
}
