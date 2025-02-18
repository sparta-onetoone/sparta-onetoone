package com.eureka.spartaonetoone.domain.cart.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CartItemUpdateRequestDto {
	@NotNull
	@JsonProperty("quantity")
	private int quantity;
}
