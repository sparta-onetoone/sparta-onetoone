package com.eureka.spartaonetoone.cart.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eureka.spartaonetoone.cart.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {

	@Query("SELECT c FROM Cart c WHERE c.cartId = :cartId AND c.isDeleted = false")
	Optional<Cart> findActiveCartById(UUID cartId);
}
