package com.eureka.spartaonetoone.domain.cart.domain.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.eureka.spartaonetoone.domain.cart.domain.Cart;

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
		Cart cart = Cart.of(userId);
		Cart savedCart = cartRepository.save(cart);

		// When & Then
		assertThat(savedCart).isNotNull()
			.extracting("userId", "isDeleted")
			.containsExactlyInAnyOrder(userId, false);
	}
}