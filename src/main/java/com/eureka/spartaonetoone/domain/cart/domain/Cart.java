package com.eureka.spartaonetoone.domain.cart.domain;

import java.util.UUID;

import com.eureka.spartaonetoone.common.utils.TimeStamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_cart")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends TimeStamp {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID cartId;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private boolean isDeleted;

	public static Cart of(UUID userId) {
		return Cart.builder()
			.userId(userId)
			.isDeleted(false)
			.build();
	}
}
