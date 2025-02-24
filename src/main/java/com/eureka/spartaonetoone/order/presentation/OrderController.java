package com.eureka.spartaonetoone.order.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.eureka.spartaonetoone.order.application.dtos.request.OrderRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderCancelRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderCreateResponseDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderSearchResponseDto;
import com.eureka.spartaonetoone.order.application.exceptions.OrderException;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

	private final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
	private final String ROLE_ADMIN = "ROLE_ADMIN";

	private final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
	private final String ROLE_ADMIN = "ROLE_ADMIN";

	private final OrderService orderService;

	@PostMapping
	@Secured({"ROLE_CUSTOMER", "ROLE_OWNER"})
	public ResponseEntity<CommonResponse<?>> saveOrder(@Valid @RequestBody OrderCreateRequestDto requestDto) {
		OrderCreateResponseDto response = OrderCreateResponseDto.from(orderService.saveOrder(requestDto));
		return ResponseEntity.ok(CommonResponse.success(response, "주문 생성 성공"));
	}

	@GetMapping("/{order_id}")
	public ResponseEntity<CommonResponse<?>> getOrder(
		@PathVariable("order_id") UUID orderId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		String userRole = getUserRole(userDetails);
		return ResponseEntity.ok(CommonResponse.success(orderService.getOrder(userRole, orderId), "주문 조회 성공"));
	}

	@GetMapping
	public ResponseEntity<CommonResponse<?>> getOrders(
		@RequestParam(value = "store_id", required = false) UUID storeId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		String userRole = getUserRole(userDetails);
		List<OrderSearchResponseDto> response;
		String message;

		if (storeId != null && userRole.equals("ROLE_OWNER")) {
			response = orderService.getOrdersByStore(storeId);
			message = "가게 주문 목록 조회 성공";
		} else if (userRole.equals("ROLE_CUSTOMER")) {
			response = orderService.getOrdersByUserId(userDetails.getUserId());
			message = "주문 목록 조회 성공";
		} else {
			response = orderService.getAllOrders();
			message = "전체 주문 목록 조회 성공";
		}
		return ResponseEntity.ok(CommonResponse.success(response, message));
	}

	@PostMapping("/request")
	public ResponseEntity<CommonResponse<?>> requestOrder(@RequestBody OrderRequestDto requestDto) {
		orderService.requestOrder(requestDto.getOrderId());
		return ResponseEntity.ok(CommonResponse.success(null, "주문 요청 성공"));
	}

	@PostMapping("/cancel")
	public ResponseEntity<CommonResponse<?>> cancelOrder(
		@RequestBody OrderCancelRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		String userRole = getUserRole(userDetails);
		if(userRole.equals(ROLE_CUSTOMER) && !requestDto.getCustomerId().equals(userDetails.getUserId())) {
			throw new OrderException.CancelPermissionDenied();
		}

		orderService.cancelOrder(requestDto.getOrderId(), requestDto.getStoreId());
		return ResponseEntity.ok(CommonResponse.success(null, "주문 취소 성공"));
	}

	@DeleteMapping("/{order_id}")
	public ResponseEntity<CommonResponse<?>> deleteOrder(
		@PathVariable("order_id") UUID orderId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		String userRole = getUserRole(userDetails);
		UUID userId = userRole.equals(ROLE_ADMIN) ? null : userDetails.getUserId();

		orderService.deleteOrder(userRole, orderId, userId);
		return ResponseEntity.ok(CommonResponse.success(null, "주문 삭제 성공"));
	}

	private String getUserRole(UserDetailsImpl userDetails) {
		return userDetails.getAuthorities().stream().findFirst().get().getAuthority();
	}
}
