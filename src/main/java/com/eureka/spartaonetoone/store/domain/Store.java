package com.eureka.spartaonetoone.store.domain;

import jakarta.persistence.*;
import com.eureka.spartaonetoone.common.utils.TimeStamp;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
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

	@Column(name = "category_ids")
	private String categoryIds; // uui1d,uuid2,uuid3

	// 1:1 관계로 Address 관리
	@OneToOne(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
	private Address address;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "state", length = 10, nullable = false)
	private StoreState state;

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

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted = false;

	// Private 생성자: 오직 정적 팩토리 메서드와 빌더를 통해서만 객체 생성
	@Builder(builderMethodName = "builder", access = AccessLevel.PRIVATE)
	private Store(UUID userId, String name, StoreState state, String tellNumber,
				  String description, Integer minOrderPrice, Integer deliveryFee,
				  Float rating, Integer reviewCount, String categoryIds) {
		this.userId = userId;
		this.name = name;
		this.state = state;
		this.tellNumber = tellNumber;
		this.description = description;
		this.minOrderPrice = minOrderPrice;
		this.deliveryFee = deliveryFee;
		this.rating = rating;
		this.reviewCount = reviewCount;
		this.categoryIds = categoryIds;
	}


	// 정적 팩토리 메서드: Service에서 DTO의 값을 추출해 호출할 수 있도록 함
	// of도 좋지만 createstore
	public static Store createStore(UUID userId, String name, StoreState state, String tellNumber,
		String description, Integer minOrderPrice, Integer deliveryFee,
		Float rating, Integer reviewCount, String categoryIds) {
		return builder()
			.userId(userId)
			.name(name)
			.state(state)
			.tellNumber(tellNumber)
			.description(description)
			.minOrderPrice(minOrderPrice != null ? minOrderPrice : 0)
			.deliveryFee(deliveryFee != null ? deliveryFee : 0)
			.rating(rating != null ? rating : 0.0f)
			.reviewCount(reviewCount != null ? reviewCount : 0)
			.categoryIds(categoryIds)
			.build();
	}

	// 엔티티 내부 업데이트 메서드: Service에서 개별 필드를 전달해 업데이트
	public void update(String name, StoreState state, String tellNumber,
					   String description, Integer minOrderPrice, Integer deliveryFee,
					   Float rating, Integer reviewCount) {
		this.name = name;
		if (state != null) {
			this.state = state;
		}
		this.tellNumber = tellNumber;
		this.description = description;
		this.minOrderPrice = minOrderPrice != null ? minOrderPrice : this.minOrderPrice;
		this.deliveryFee = deliveryFee != null ? deliveryFee : this.deliveryFee;
		this.rating = rating != null ? rating : this.rating;
		this.reviewCount = reviewCount != null ? reviewCount : this.reviewCount;
	}

	public void softDelete(UUID deletedByUser) {
		this.isDeleted = true;
		this.deletedBy = deletedByUser;
		this.deletedAt = LocalDateTime.now();
	}
}
