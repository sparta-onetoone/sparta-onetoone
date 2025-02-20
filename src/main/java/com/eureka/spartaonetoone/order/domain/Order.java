package com.eureka.spartaonetoone.order.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.eureka.spartaonetoone.common.utils.TimeStamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "p_order")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends TimeStamp {

	@Id
	@UuidGenerator
	private UUID orderId;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private UUID storeId;

	@Enumerated(EnumType.STRING)
	private OrderType type;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(length = 50)
	private String requests;

	@OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
	private Set<OrderItem> orderItems;

	@Column(nullable = false)
	private int totalPrice;

	@Column(nullable = false)
	private boolean isDeleted;

	public void addOrderItem(OrderItem orderItem) {
		orderItems.add(orderItem);
	}

	public void calculateTotalPrice() {
		totalPrice = orderItems.stream()
			.mapToInt(OrderItem::getPrice)
			.sum();
	}

	public void delete() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
		this.status = OrderStatus.CANCELED;
		this.orderItems.forEach(OrderItem::delete);
	}

	public static Order createOrder(UUID userId, UUID storeId, OrderType type, String requests) {
		return Order.builder()
			.userId(userId)
			.storeId(storeId)
			.type(type)
			.status(OrderStatus.PENDING)
			.requests(requests)
			.orderItems(new HashSet<>())
			.totalPrice(0)
			.isDeleted(false)
			.build();
	}
}
