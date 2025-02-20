package com.eureka.spartaonetoone.domain.cart.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartCreateRequestDto {

	@NotNull
	@JsonProperty("user_id")
	private UUID userId;
}
