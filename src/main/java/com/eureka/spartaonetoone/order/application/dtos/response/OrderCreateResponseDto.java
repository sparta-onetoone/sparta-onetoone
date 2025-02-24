package com.eureka.spartaonetoone.order.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.order.domain.Order;
import com.eureka.spartaonetoone.order.domain.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OrderCreateResponseDto {

	@JsonProperty("order_id")
	private UUID orderId;
	private OrderStatus status;

	public static OrderCreateResponseDto from(Order order) {
		return OrderCreateResponseDto.builder()
			.orderId(order.getOrderId())
			.status(order.getStatus())
			.build();
	}
}
