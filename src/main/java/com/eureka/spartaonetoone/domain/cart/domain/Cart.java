package com.eureka.spartaonetoone.domain.cart.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.eureka.spartaonetoone.common.utils.TimeStamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	@UuidGenerator
	private UUID cartId;

	@Column(nullable = false)
	private UUID userId;

	@OneToMany(mappedBy = "cart")
	private List<CartItem> cartItems;

	@Column(nullable = false)
	private boolean isDeleted;

	public static Cart of(UUID userId) {
		return Cart.builder()
			.userId(userId)
			.cartItems(new ArrayList<>())
			.isDeleted(false)
			.build();
	}

	public void addCartItem(CartItem cartItem) {
		this.cartItems.add(cartItem);
	}
}
