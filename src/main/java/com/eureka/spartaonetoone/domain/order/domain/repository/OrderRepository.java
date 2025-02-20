package com.eureka.spartaonetoone.domain.order.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eureka.spartaonetoone.domain.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

	@Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.isDeleted = false")
	Optional<Order> findActiveOrderById(UUID orderId);
}
