package com.eureka.spartaonetoone.order.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.order.application.OrderService;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderCreateRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderCreateResponseDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderSearchResponseDto;

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
	public ResponseEntity<CommonResponse<?>> getOrders(
		@RequestParam(value = "store_id", required = false) UUID storeId
	) {
		// TODO : User Role에 따라 다른 주문 목록 조회
		List<OrderSearchResponseDto> response;
		String message;

		if (storeId != null) {
			// TODO : User Role이 OWNER인 경우 확인 필요
			response = orderService.getOrdersByStore(storeId);
			message = "가게 주문 목록 조회 성공";
		} else {
			response = orderService.getOrdersByUserRole();
			message = "주문 목록 조회 성공";
		}
		return ResponseEntity.ok(CommonResponse.success(response, message));
	}

	@DeleteMapping("/{order_id}")
	public ResponseEntity<CommonResponse<?>> deleteOrder(@PathVariable("order_id") UUID orderId) {
		orderService.deleteOrder(orderId);
		return ResponseEntity.ok(CommonResponse.success(null, "주문 삭제 성공"));
	}
}
