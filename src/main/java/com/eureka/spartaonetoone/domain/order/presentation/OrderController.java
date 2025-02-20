package com.eureka.spartaonetoone.domain.order.presentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.order.application.OrderService;
import com.eureka.spartaonetoone.domain.order.application.dtos.request.OrderCreateRequestDto;
import com.eureka.spartaonetoone.domain.order.application.dtos.response.OrderCreateResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<CommonResponse<?>> saveOrder(@Valid @RequestBody OrderCreateRequestDto requestDto) {
		OrderCreateResponseDto response = OrderCreateResponseDto.from(orderService.saveOrder(requestDto));
		return ResponseEntity.ok(CommonResponse.success(response, "주문 생성 성공"));
	}

	@GetMapping("/{order_id}")
	public ResponseEntity<CommonResponse<?>> getOrder(@PathVariable("order_id") UUID orderId) {
		return ResponseEntity.ok(CommonResponse.success(orderService.getOrder(orderId), "주문 조회 성공"));
	}

	@GetMapping
	public ResponseEntity<CommonResponse<?>> getOrders() {
		return ResponseEntity.ok(CommonResponse.success(orderService.getOrdersByUserRole(), "주문 목록 조회 성공"));
	}
}
