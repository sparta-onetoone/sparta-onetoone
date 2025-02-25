package com.eureka.spartaonetoone.common.client;

import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.eureka.spartaonetoone.common.dtos.response.CartResponse;
import com.eureka.spartaonetoone.common.utils.CommonResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartClient {

	private final WebClient webClient;
	private static final String CART_URL = "/api/v1/carts";

	public CartResponse.Read getCart(UUID cartId) {
		return webClient.get()
			.uri(CART_URL + "/{cart_id}", cartId)
			.retrieve()
			.bodyToMono(
				new ParameterizedTypeReference<CommonResponse<CartResponse.Read>>() {
			})
			.map(CommonResponse::getData)
			.block();
	}

	public void deleteCart(UUID cartId, UUID userId) {
		webClient.delete()
			.uri(CART_URL + "/{cart_id}", cartId)
			.header("X-User-Id", userId.toString())
			.retrieve()
			.bodyToMono(Void.class)
			.block();
	}
}
