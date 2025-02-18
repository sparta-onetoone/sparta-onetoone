package com.eureka.spartaonetoone.store.domain.entity;

import jakarta.persistence.*;
import com.eureka.spartaonetoone.common.utils.TimeStamp;
import com.eureka.spartaonetoone.store.application.dto.StoreRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_store")
public class Store extends TimeStamp {

	@Id
	@GeneratedValue
	@Column(name = "store_id")
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	// Category를 직접 Category 객체 대신 UUID로 저장
	@Column(name = "category_id", nullable = false)
	private UUID categoryId;

	// 1:1 관계로 Address 관리 (변경 필요시 내부 로직으로 처리)
	@OneToOne(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	private Address address;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "state", length = 10, nullable = false)
	private String state;

	@Column(name = "tell_number", length = 20, nullable = false)
	private String tellNumber;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "min_order_price", nullable = false)
	private Integer minOrderPrice;

	@Column(name = "delivery_fee", nullable = false)
	private Integer deliveryFee;

	@Column(name = "rating", nullable = false)
	private Float rating;

	@Column(name = "review_count", nullable = false)
	private Integer reviewCount;

	// Private 생성자 : 오직 정적 팩토리 메서드와 빌더를 통해서만 객체를 생성하도록 함
	@Builder(builderMethodName = "builder", access = AccessLevel.PRIVATE)
	private Store(UUID userId, UUID orderId, String name, String state, String tellNumber,
		String description, Integer minOrderPrice, Integer deliveryFee,
		Float rating, Integer reviewCount, UUID categoryId) {
		this.userId = userId;
		this.orderId = orderId;
		this.name = name;
		this.state = state;
		this.tellNumber = tellNumber;
		this.description = description;
		this.minOrderPrice = minOrderPrice;
		this.deliveryFee = deliveryFee;
		this.rating = rating;
		this.reviewCount = reviewCount;
		this.categoryId = categoryId;
	}

	// 정적 팩토리 메서드 - 클라이언트가 전달한 StoreRequestDto를 기반으로 내부 빌더를 활용하여 새로운 Store 객체를 생성
	public static Store from(final StoreRequestDto storeRequestDto) {
		return com.eureka.spartaonetoone.store.domain.entity.Store.builder()
			.userId(storeRequestDto.getUserId())
			.orderId(storeRequestDto.getOrderId())
			.name(storeRequestDto.getName())
			.state(storeRequestDto.getState() != null ? storeRequestDto.getState() : "OPEN")
			.tellNumber(storeRequestDto.getTellNumber())
			.description(storeRequestDto.getDescription())
			.minOrderPrice(storeRequestDto.getMinOrderPrice() != null ? storeRequestDto.getMinOrderPrice() : 0)
			.deliveryFee(storeRequestDto.getDeliveryFee() != null ? storeRequestDto.getDeliveryFee() : 0)
			.rating(storeRequestDto.getRating() != null ? storeRequestDto.getRating() : 0.0f)
			.reviewCount(storeRequestDto.getReviewCount() != null ? storeRequestDto.getReviewCount() : 0)
			.categoryId(storeRequestDto.getCategoryId())
			.build();
	}

	// 엔티티 내부 업데이트 메서드 - 기존 Store 객체의 필드를 전달받은 StoreRequestDto의 값으로 업데이트
	public void updateFrom(final StoreRequestDto storeRequestDto) {
		this.name = storeRequestDto.getName();
		this.state = storeRequestDto.getState() != null ? storeRequestDto.getState() : this.state;
		this.tellNumber = storeRequestDto.getTellNumber();
		this.description = storeRequestDto.getDescription();
		this.minOrderPrice = storeRequestDto.getMinOrderPrice() != null ? storeRequestDto.getMinOrderPrice() : this.minOrderPrice;
		this.deliveryFee = storeRequestDto.getDeliveryFee() != null ? storeRequestDto.getDeliveryFee() : this.deliveryFee;
		this.rating = storeRequestDto.getRating() != null ? storeRequestDto.getRating() : this.rating;
		this.reviewCount = storeRequestDto.getReviewCount() != null ? storeRequestDto.getReviewCount() : this.reviewCount;
		this.categoryId = storeRequestDto.getCategoryId();
	}
}
