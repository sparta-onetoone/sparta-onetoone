package com.eureka.spartaonetoone.store.application.dto;

import com.eureka.spartaonetoone.store.domain.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponseDto {
	private UUID id;
	private UUID userId;
	private UUID orderId;
	private String name;
	private String state;
	private String tellNumber;
	private UUID addressId;    // Address 엔티티가 연결되어 있다면 해당 id를 설정
	private String description;
	private Integer minOrderPrice;
	private Integer deliveryFee;
	private Float rating;
	private Integer reviewCount;
	private UUID categoryId;   // Category 엔티티가 연결되어 있다면 해당 id를 설정
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public static StoreResponseDto from(final Store store) {
		return StoreResponseDto.builder()
			.id(store.getId())
			.userId(store.getUserId())
			.orderId(store.getOrderId())
			.name(store.getName())
			.state(store.getState())
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
