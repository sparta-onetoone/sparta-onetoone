package com.eureka.spartaonetoone.store.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {
	private UUID userId;
	private UUID orderId;
	private String name;
	private String state;
	private String tellNumber;
	private String description;
	private Integer minOrderPrice;
	private Integer deliveryFee;
	private Float rating;
	private Integer reviewCount;
	private String createdBy;
	// 카테고리 엔티티와 연관된 값을 전달받기 위한 필드
	private UUID categoryId;
}
