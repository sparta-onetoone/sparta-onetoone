package com.eureka.spartaonetoone.store.application.dtos.response;

import com.eureka.spartaonetoone.store.domain.Store;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
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
	private List<String>  categoryIds;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;


	public static StoreResponseDto from(final Store store) {
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
			.categoryIds(parseCategoryIds(store.getCategoryIds()))
			.createdAt(store.getCreatedAt())
			.updatedAt(store.getUpdatedAt())
			.deletedAt(store.getDeletedAt())
			.build();
	}

	private static List<String> parseCategoryIds(String categoryIds) {
		return (categoryIds != null && !categoryIds.isEmpty())
				? List.of(categoryIds.split(","))
				: List.of();
	}
}
