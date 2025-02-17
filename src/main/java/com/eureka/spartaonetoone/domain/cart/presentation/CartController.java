package com.eureka.spartaonetoone.domain.cart.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.cart.application.CartService;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.response.CartCreateResponseDto;

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
}
