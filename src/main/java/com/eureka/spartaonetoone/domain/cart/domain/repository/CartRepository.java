package com.eureka.spartaonetoone.domain.cart.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eureka.spartaonetoone.domain.cart.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {
}
