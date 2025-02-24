package com.eureka.spartaonetoone.common.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
	// 해당 가게의 식별자
	private UUID storeId;
}
