package com.eureka.spartaonetoone.order.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.eureka.spartaonetoone.cart.application.dtos.response.CartCreateResponseDto;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderCreateRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderCancelRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderCreateResponseDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderSearchResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "주문 API", description = "주문 관련 API")
public interface OrderApi {

	@Operation(summary = "주문 생성", description = "주문을 생성하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문 생성 성공",
			content = @Content(schema = @Schema(implementation = OrderCreateResponseDto.class)))
	})
	ResponseEntity<CommonResponse<?>> saveOrder(OrderCreateRequestDto requestDto);

	@Operation(summary = "주문 단건 조회", description = "주문을 1건 조회하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문 조회 성공",
			content = @Content(schema = @Schema(implementation = OrderSearchResponseDto.class)))
	})
	ResponseEntity<CommonResponse<?>> getOrder(UUID orderId, UserDetailsImpl userDetails);

	@Operation(summary = "주문 목록 조회", description = "주문 목록을 조회하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문 조회 성공",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderSearchResponseDto.class))))
	})
	ResponseEntity<CommonResponse<?>> getOrders(UUID storeId, UserDetailsImpl userDetails);

	@Operation(summary = "주문 요청", description = "주문을 요청하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문 요청 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<?>> requestOrder(OrderRequestDto requestDto);

	@Operation(summary = "주문 취소", description = "주문을 취소하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문 취소 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<?>> cancelOrder(OrderCancelRequestDto requestDto,	UserDetailsImpl userDetails);

	@Operation(summary = "주문 삭제", description = "주문을 삭제하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문 삭제 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<?>> deleteOrder(UUID orderId, UserDetailsImpl userDetails);
}
