package com.eureka.spartaonetoone.order.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eureka.spartaonetoone.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

	@Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.isDeleted = false")
	Optional<Order> findActiveOrderById(UUID orderId);

	@Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.isDeleted = false")
	List<Order> findAllActiveOrderByUserId(UUID userId);

	@Query("SELECT o FROM Order o WHERE o.storeId = :storeId AND o.isDeleted = false")
	List<Order> findAllActiveOrderByStoreId(UUID storeId);
}
