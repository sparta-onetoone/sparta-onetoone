package com.eureka.spartaonetoone.domain.cart.application;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartItemCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartItemUpdateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.exceptions.CartException;
import com.eureka.spartaonetoone.domain.cart.application.exceptions.CartItemException;
import com.eureka.spartaonetoone.domain.cart.domain.Cart;
import com.eureka.spartaonetoone.domain.cart.domain.CartItem;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class CartServiceTest {

	@Autowired
	private CartService cartService;

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

	@DisplayName("장바구니가 존재하지 않는 경우, 장바구니 상품을 추가할 수 없다.")
	@Test
	void cart_for_cart_item_not_found_exception_test() {
		// Given
		CartItemCreateRequestDto requestDto = CartItemCreateRequestDto.of(UUID.randomUUID(), 1);

		// When & Then
		UUID invalidCartId = UUID.randomUUID();
		assertThatThrownBy(() -> cartService.saveCartItem(invalidCartId, requestDto))
			.isInstanceOf(CartException.NotFound.class);
	}

	@DisplayName("장바구니 상품의 개수가 1개 미만일 경우, 예외가 발생한다.")
	@Test
	void cart_item_min_quantity_exception_test() {
		// Given
		UUID userId = UUID.randomUUID();
		CartCreateRequestDto request = CartCreateRequestDto.builder()
			.userId(userId)
			.build();
		Cart savedCart = cartService.saveCart(request);
		CartItemCreateRequestDto requestDto = CartItemCreateRequestDto.of(UUID.randomUUID(), 0);

		// When & Then
		assertThatThrownBy(() -> cartService.saveCartItem(savedCart.getCartId(), requestDto))
			.isInstanceOf(CartItemException.MinQuantity.class);
	}

	@DisplayName("수정하는 장바구니 상품의 개수가 1개 미만일 경우, 예외가 발생한다.")
	@Test
	void cart_item_update_min_quantity_excepion_test() {
		// Given
		UUID userId = UUID.randomUUID();
		CartCreateRequestDto request = CartCreateRequestDto.builder()
			.userId(userId)
			.build();
		Cart savedCart = cartService.saveCart(request);
		CartItem cartItem = CartItem.of(
			savedCart,
			UUID.randomUUID(),
			"product",
			"image",
			1,
			1000
		);
		savedCart.addCartItem(cartItem);

		CartItemUpdateRequestDto requestDto = CartItemUpdateRequestDto.builder()
			.quantity(0)
			.build();

		// When & Then
		assertThatThrownBy(() ->
			cartService.updateCartItems(savedCart.getCartId(), cartItem.getCartItemId(), requestDto))
			.isInstanceOf(CartItemException.MinQuantity.class);
	}

	@Transactional
	@DisplayName("장바구니를 삭제하면 soft delete 가 되어야 한다.")
	@Test
	void cart_soft_delete_test() {
		// Given
		UUID userId = UUID.randomUUID();
		CartCreateRequestDto request = CartCreateRequestDto.builder()
			.userId(userId)
			.build();
		Cart savedCart = cartService.saveCart(request);

		// When
		cartService.deleteCart(savedCart.getCartId());

		// Then
		assertThat(savedCart).isNotNull()
			.extracting("isDeleted")
			.isEqualTo(true);
	}
}









