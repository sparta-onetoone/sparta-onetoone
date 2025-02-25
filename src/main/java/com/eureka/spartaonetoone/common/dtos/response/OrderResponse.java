package com.eureka.spartaonetoone.common.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

	@JsonProperty("order_id")
	private UUID orderId;

	@Value
	public static class GetStoreId {

		@JsonProperty("store_id")
		UUID storeId;
	}
}
