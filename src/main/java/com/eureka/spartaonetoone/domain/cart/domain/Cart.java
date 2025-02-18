package com.eureka.spartaonetoone.domain.cart.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.eureka.spartaonetoone.common.utils.TimeStamp;

import jakarta.persistence.CascadeType;
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

	@OneToMany(mappedBy = "cart", cascade = CascadeType.PERSIST)
	private Set<CartItem> cartItems;

	@Column(nullable = false)
	private boolean isDeleted;

	public void addCartItem(CartItem cartItem) {
		this.cartItems.add(cartItem);
	}

	public void updateCartItem(CartItem cartItem, int quantity) {
		cartItem.updateQuantity(quantity);
		if (cartItem.isDeleted()) {
			this.cartItems.remove(cartItem);
		}
	}

	public void delete() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
		this.cartItems.forEach(CartItem::delete);
	}

	public static Cart of(UUID userId) {
		return Cart.builder()
			.userId(userId)
			.cartItems(new HashSet<>())
			.isDeleted(false)
			.build();
	}
}
