package com.eureka.spartaonetoone.domain.order.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eureka.spartaonetoone.domain.order.domain.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
