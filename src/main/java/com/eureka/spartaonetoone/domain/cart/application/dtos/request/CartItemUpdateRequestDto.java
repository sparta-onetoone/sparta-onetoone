package com.eureka.spartaonetoone.domain.cart.application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemUpdateRequestDto {
	@NotNull
	@JsonProperty("quantity")
	private int quantity;
}
