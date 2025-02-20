package com.eureka.spartaonetoone.domain.order.presentation;

import org.springframework.http.ResponseEntity;
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
}
