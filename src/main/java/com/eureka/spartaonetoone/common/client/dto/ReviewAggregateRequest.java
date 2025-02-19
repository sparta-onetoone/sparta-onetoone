package com.eureka.spartaonetoone.common.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAggregateRequest {
	// 해당 가게의 식별자
	private UUID storeId;
}
