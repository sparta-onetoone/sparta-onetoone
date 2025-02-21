package com.eureka.spartaonetoone.domain.cart.domain.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.eureka.spartaonetoone.cart.domain.Cart;
import com.eureka.spartaonetoone.cart.domain.repository.CartRepository;

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {

	@Autowired
	private CartRepository cartRepository;

	@DisplayName("사용자 id를 통해 장바구니를 생성한다.")
	@Test
	void cart_save_by_user_id_test() {
		// Given
		UUID userId = UUID.randomUUID();
		Cart cart = Cart.createCart(userId);
		Cart savedCart = cartRepository.save(cart);

		// When & Then
		assertThat(savedCart).isNotNull()
			.extracting("userId", "isDeleted")
			.containsExactlyInAnyOrder(userId, false);
	}

	@DisplayName("사용자는 장바구니 id를 통해 장바구니를 조회할 수 있다.")
	@Test
	void test() {
		// Given
		UUID userId = UUID.randomUUID();
		Cart cart = Cart.createCart(userId);
		Cart savedCart = cartRepository.save(cart);

		// When
		Cart foundCart = cartRepository.findActiveCartById(savedCart.getCartId()).orElse(null);

		// Then
		assertThat(foundCart).isNotNull()
			.extracting("userId", "isDeleted")
			.containsExactlyInAnyOrder(userId, false);
	}

	@DisplayName("사용자는 삭제 처리(soft delete)된 장바구니를 조회할 수 없다.")
	@Test
	void cart_soft_delete_test() {
		// Given
		UUID userId = UUID.randomUUID();
		Cart cart = Cart.createCart(userId);
		Cart savedCart = cartRepository.save(cart);

		// When
		savedCart.delete();
		Cart deletedCart = cartRepository.findActiveCartById(savedCart.getCartId()).orElse(null);

		// Then
		assertThat(deletedCart).isNull();
	}
}