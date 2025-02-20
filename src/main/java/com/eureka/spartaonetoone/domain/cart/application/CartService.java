package com.eureka.spartaonetoone.domain.cart.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.domain.cart.application.dtos.CartItemInfo;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartItemCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartItemUpdateRequestDto;
import com.eureka.spartaonetoone.domain.cart.application.dtos.response.CartSearchResponseDto;
import com.eureka.spartaonetoone.domain.cart.application.exceptions.CartException;
import com.eureka.spartaonetoone.domain.cart.application.exceptions.CartItemException;
import com.eureka.spartaonetoone.domain.cart.domain.Cart;
import com.eureka.spartaonetoone.domain.cart.domain.CartItem;
import com.eureka.spartaonetoone.domain.cart.domain.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final int MIN_QUANTITY = 1;

	private final CartRepository cartRepository;

	public Cart saveCart(CartCreateRequestDto requestDto) {
		Cart cart = Cart.of(requestDto.getUserId());
		return cartRepository.save(cart);
	}

	@Transactional
	public void saveCartItem(UUID cartId, CartItemCreateRequestDto requestDto) {
		Cart cart = cartRepository.findActiveCartById(cartId)
			.orElseThrow(CartException.NotFound::new);

		if (requestDto.getQuantity() < MIN_QUANTITY) {
			throw new CartItemException.MinQuantity();
		}

		CartItem cartItem = createCartItem(cart, requestDto);
		cart.addCartItem(cartItem);
	}

	@Transactional(readOnly = true)
	public CartSearchResponseDto getCart(UUID cartId) {
		Cart cart = cartRepository.findActiveCartById(cartId)
			.orElseThrow(CartException.NotFound::new);

		List<CartItemInfo> cartItems = cart.getCartItems().stream()
			.filter(cartItem -> !cartItem.isDeleted())
			.map(CartItemInfo::from)
			.toList();

		return CartSearchResponseDto.of(cart, cartItems);
	}

	@Transactional
	public void updateCartItems(UUID cartId, UUID cartItemId, CartItemUpdateRequestDto requestDto) {
		Cart cart = cartRepository.findActiveCartById(cartId)
			.orElseThrow(CartException.NotFound::new);

		int quantity = requestDto.getQuantity();

		// TODO : Product의 남은 수량을 확인하여 수량이 부족하다면 예외 처리하기

		if(quantity < MIN_QUANTITY) {
			throw new CartItemException.MinQuantity();
		}

		CartItem cartItem = cart.getCartItems().stream()
				.filter(item -> item.getCartItemId().equals(cartItemId))
				.findFirst()
				.orElseThrow(CartItemException.NotFound::new);

		cart.updateCartItem(cartItem, quantity);
	}

	@Transactional
	public void deleteCartItem(UUID cartId, UUID cartItemId) {
		Cart cart = cartRepository.findActiveCartById(cartId)
			.orElseThrow(CartException.NotFound::new);

		CartItem cartItem = cart.getCartItems().stream()
			.filter(item -> item.getCartItemId().equals(cartItemId))
			.findFirst()
			.orElseThrow(CartItemException.NotFoundInCart::new);

		cartItem.delete();
	}

	@Transactional
	public void deleteCart(UUID cartId) {
		Cart cart = cartRepository.findActiveCartById(cartId)
			.orElseThrow(CartException.NotFound::new);

		cart.delete();
	}

	private CartItem createCartItem(Cart cart, CartItemCreateRequestDto requestDto) {
		/*
		 TODO-1 : Product domain에서 상품 정보를 가져와 CartItem 생성하기
		 TODO-2 : 중복된 상품이 있는지 확인하고 중복된 상품이 있다면 수량과 가격만 증가시키기
		 */
		UUID productId = UUID.randomUUID();
		String productName = "상품 이름";
		String productImage = "상품 이미지";
		int stock = 0; // 현재 상품의 재고
		int productPrice = 10000;
		int quantity = requestDto.getQuantity();
		int price = quantity * productPrice;

		return CartItem.of(cart, productId, productName, productImage, quantity, price);
	}
}
