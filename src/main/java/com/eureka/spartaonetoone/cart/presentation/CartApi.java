package com.eureka.spartaonetoone.cart.presentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.eureka.spartaonetoone.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.cart.application.dtos.request.CartItemCreateRequestDto;
import com.eureka.spartaonetoone.cart.application.dtos.request.CartItemUpdateRequestDto;
import com.eureka.spartaonetoone.cart.application.dtos.response.CartCreateResponseDto;
import com.eureka.spartaonetoone.cart.application.dtos.response.CartSearchResponseDto;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "장바구니 API", description = "장바구니 관련 API")
public interface CartApi {

	@Operation(summary = "장바구니 생성", description = "장바구니를 생성하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 생성 성공",
			content = @Content(schema = @Schema(implementation = CartCreateResponseDto.class)))
	})
	ResponseEntity<CommonResponse<?>> saveCart(CartCreateRequestDto requestDto);

	@Operation(summary = "장바구니 조회", description = "장바구니를 조회하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 조회 성공",
			content = @Content(schema = @Schema(implementation = CartSearchResponseDto.class)))
	})
	ResponseEntity<CommonResponse<?>> getCart(UUID cartId);

	@Operation(summary = "장바구니 삭제", description = "장바구니를 삭제하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 삭제 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<?>> deleteCart(UUID cartId, UserDetailsImpl userDetails);

	@Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 상품 추가 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<?>> saveCartItem(UUID cartId, CartItemCreateRequestDto requestDto);

	@Operation(summary = "장바구니 상품 수정", description = "장바구니에 존재하는 상품을 수정하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 상품 수정 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<?>> updateCartItem(UUID cartId, UUID cartItemId, CartItemUpdateRequestDto requestDto);

	@Operation(summary = "장바구니 상품 삭제", description = "장바구니에 존재하는 상품을 삭제하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "장바구니 상품 수정 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<?>> deleteCartItem(UUID cartId, UUID cartItemId, UserDetailsImpl userDetails);
}
