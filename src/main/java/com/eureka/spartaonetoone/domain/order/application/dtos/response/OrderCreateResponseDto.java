package com.eureka.spartaonetoone.domain.order.application.dtos.response;

import java.util.UUID;

import com.eureka.spartaonetoone.domain.order.domain.Order;
import com.eureka.spartaonetoone.domain.order.domain.OrderStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OrderCreateResponseDto {

	private UUID orderId;
	private OrderStatus status;

	public static OrderCreateResponseDto from(Order order) {
		return OrderCreateResponseDto.builder()
			.orderId(order.getOrderId())
			.status(order.getStatus())
			.build();
	}
}
