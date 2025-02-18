package com.eureka.spartaonetoone.domain.cart.presentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.cart.application.CartService;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartItemCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartItemUpdateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.response.CartCreateResponseDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.response.CartSearchResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping
	public ResponseEntity<CommonResponse<?>> saveCart(@RequestBody CartCreateRequestDto requestDto) {
		CartCreateResponseDto response = CartCreateResponseDto.from(cartService.saveCart(requestDto));
		return ResponseEntity.ok(CommonResponse.success(response, "장바구니 생성 성공"));
	}

	@GetMapping("/{cart_id}")
	public ResponseEntity<CommonResponse<?>> getCart(@PathVariable("cart_id") UUID cartId) {
		CartSearchResponseDto response = cartService.getCart(cartId);
		return ResponseEntity.ok(CommonResponse.success(response, "장바구니 조회 성공"));
	}

	@PostMapping("/{cart_id}/items")
	public ResponseEntity<CommonResponse<?>> saveCartItem(
		@PathVariable("cart_id") UUID cartId,
		@RequestBody CartItemCreateRequestDto requestDto
	) {
		cartService.saveCartItem(cartId, requestDto);
		return ResponseEntity.ok(CommonResponse.success(null, "장바구니 상품 추가 성공"));
	}

	@PostMapping("/{cart_id}/item")
	public ResponseEntity<CommonResponse<?>> updateCartItem(
		@PathVariable("cart_id") UUID cartId,
		@RequestBody CartItemUpdateRequestDto requestDto
	) {
		cartService.updateCartItems(cartId, requestDto);
		return ResponseEntity.ok(CommonResponse.success(null, "장바구니 상품 수정 성공"));
	}
}
