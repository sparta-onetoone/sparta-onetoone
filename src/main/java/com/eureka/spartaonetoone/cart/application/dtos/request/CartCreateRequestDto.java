package com.eureka.spartaonetoone.cart.application.dtos.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartCreateRequestDto {

	@NotNull
	@JsonProperty("user_id")
	private UUID userId;
}
