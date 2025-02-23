package com.eureka.spartaonetoone.common.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
	// 주문의 식별자
	private UUID orderId;
}
