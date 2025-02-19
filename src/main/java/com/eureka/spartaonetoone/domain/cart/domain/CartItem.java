package com.eureka.spartaonetoone.domain.cart.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.eureka.spartaonetoone.common.utils.TimeStamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_cart_item")
@EqualsAndHashCode(of = "cartItemId", callSuper = false)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends TimeStamp {

	@Id
	@UuidGenerator
	private UUID cartItemId;

	@ManyToOne
	@JoinColumn(name = "cart_id")
	private Cart cart;

	@Column(nullable = false)
	private UUID productId;

	@Column(nullable = false)
	private String productName;

	private String productImage;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private boolean isDeleted;

	public void updateQuantity(int quantity) {
		this.price = (this.price / this.quantity) * quantity; // TODO : 로직 수정
		this.quantity = quantity;
	}

	public void delete() {
		this.quantity = 0;
		this.price = 0;
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	public static CartItem of(Cart cart, UUID productId, String productName, String productImage, int quantity,
		int price) {
		return CartItem.builder()
			.cart(cart)
			.productId(productId)
			.productName(productName)
			.productImage(productImage)
			.quantity(quantity)
			.price(price)
			.isDeleted(false)
			.build();
	}
}
