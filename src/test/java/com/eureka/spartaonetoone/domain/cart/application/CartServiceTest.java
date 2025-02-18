package com.eureka.spartaonetoone.domain.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartItemCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.domain.Cart;
import com.eureka.spartaonetoone.domain.cart.domain.repository.CartItemRepository;
import com.eureka.spartaonetoone.domain.cart.domain.repository.CartRepository;

@SpringBootTest
@ActiveProfiles("test")
class CartServiceTest {

	@Autowired
	private CartService cartService;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	@AfterEach
	void tearDown() {
		cartRepository.deleteAllInBatch();
		cartItemRepository.deleteAllInBatch();
	}

	@DisplayName("사용자 id를 통해 장바구니를 생성한다.")
	@Test
	void cart_save_by_user_id_test() {
		// Given
		UUID userId = UUID.randomUUID();
		CartCreateRequestDto request = CartCreateRequestDto.builder()
			.userId(userId)
			.build();

		// When
		Cart savedCart = cartService.saveCart(request);

		// Then
		assertThat(savedCart).isNotNull()
			.extracting("cartId", "userId", "isDeleted")
			.containsExactlyInAnyOrder(savedCart.getCartId(), userId, false);
	}

	@DisplayName("장바구니 id와 상품 id, 상품 수량을 통해 장바구니 상품을 추가한다.")
	@Test
	void cart_item_save_test() {
		// Given
		UUID userId = UUID.randomUUID();
		CartCreateRequestDto cartRequest = CartCreateRequestDto.builder()
			.userId(userId)
			.build();
		Cart savedCart = cartService.saveCart(cartRequest);

		CartItemCreateRequestDto cartItemRequest = CartItemCreateRequestDto.of(UUID.randomUUID(), 1);

		// When
		cartService.saveCartItem(savedCart.getCartId(), cartItemRequest);

		// Then
		assertThat(savedCart.getCartItems()).hasSize(1);
	}

	@DisplayName("장바구니 id를 통해 장바구니를 조회한다.")
	@Test
	void test() {
		// Given
		UUID userId = UUID.randomUUID();
		CartCreateRequestDto request = CartCreateRequestDto.builder()
			.userId(userId)
			.build();
		Cart savedCart = cartService.saveCart(request);

		// When

		// Then

	}
}