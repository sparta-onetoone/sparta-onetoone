package com.eureka.spartaonetoone.store.application.dto;

import com.eureka.spartaonetoone.store.domain.entity.Store;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(builderMethodName = "Builder", access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponseDto {
	private UUID storeId;
	private UUID userId;
	private String name;
	private String state;
	private String tellNumber;
	private UUID addressId;
	private String description;
	private Integer minOrderPrice;
	private Integer deliveryFee;
	private Float rating;
	private Integer reviewCount;
	private UUID categoryId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	// 여기서는 from이 맞다. 하나니깐
	public static StoreResponseDto of(final Store store) {
		return Builder()
			.storeId(store.getId())
			.userId(store.getUserId())
			.name(store.getName())
			.state(store.getState().name()) // ENUM -> String
			.tellNumber(store.getTellNumber())
			.addressId(store.getAddress() != null ? store.getAddress().getId() : null)
			.description(store.getDescription())
			.minOrderPrice(store.getMinOrderPrice())
			.deliveryFee(store.getDeliveryFee())
			.rating(store.getRating())
			.reviewCount(store.getReviewCount())
			.categoryId(store.getCategoryId())
			.createdAt(store.getCreatedAt())
			.updatedAt(store.getUpdatedAt())
			.deletedAt(store.getDeletedAt())
			.build();
	}
}
