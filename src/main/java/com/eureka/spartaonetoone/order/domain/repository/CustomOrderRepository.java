package com.eureka.spartaonetoone.order.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eureka.spartaonetoone.order.domain.Order;

public interface CustomOrderRepository {

	Page<Order> searchOrders(String userRole, UUID storeId, UUID userId, Pageable pageable);
}
