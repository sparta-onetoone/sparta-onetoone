package com.eureka.spartaonetoone.domain.order.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_order_item")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem extends TimeStamp {

	@Id
	@UuidGenerator
	private UUID orderItemId;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

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

	public void delete() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	public static OrderItem createOrderItem(Order order, UUID productId, String productName, String productImage, int quantity, int price) {
		return OrderItem.builder()
			.order(order)
			.productId(productId)
			.productName(productName)
			.productImage(productImage)
			.quantity(quantity)
			.price(price)
			.isDeleted(false)
			.build();
	}
}
