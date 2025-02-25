package com.eureka.spartaonetoone.cart.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.cart.application.dtos.CartItemInfo;
import com.eureka.spartaonetoone.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.cart.application.dtos.request.CartItemCreateRequestDto;
import com.eureka.spartaonetoone.cart.application.dtos.request.CartItemUpdateRequestDto;
import com.eureka.spartaonetoone.cart.application.dtos.response.CartSearchResponseDto;
import com.eureka.spartaonetoone.cart.application.exceptions.CartException;
import com.eureka.spartaonetoone.cart.application.exceptions.CartItemException;
import com.eureka.spartaonetoone.cart.domain.Cart;
import com.eureka.spartaonetoone.cart.domain.CartItem;
import com.eureka.spartaonetoone.cart.domain.repository.CartRepository;
import com.eureka.spartaonetoone.common.client.ProductClient;
import com.eureka.spartaonetoone.common.dtos.response.ProductResponse;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final int MIN_QUANTITY = 1;
	private final String FAIL_CODE = "F000";

	private final ProductClient productClient;
	private final CartRepository cartRepository;

	@Transactional
	public Cart saveCart(CartCreateRequestDto requestDto) {
		if(cartRepository.existsActiveCartByUserId(requestDto.getUserId())) {
			throw new CartException.AlreadyExists();
		}

		Cart cart = Cart.createCart(requestDto.getUserId());
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
		if(quantity < MIN_QUANTITY) {
			throw new CartItemException.MinQuantity();
		}

		CartItem cartItem = cart.getCartItems().stream()
				.filter(item -> item.getCartItemId().equals(cartItemId))
				.findFirst()
				.orElseThrow(CartItemException.NotFound::new);

		ProductResponse.Get product = getProduct(cartItem.getProductId());
		if (product.getQuantity() < quantity) {
			throw new CartItemException.NotEnoughStock();
		}

		cartItem.updateQuantityAndPrice(quantity);
	}

	@Transactional
	public void deleteCartItem(UUID cartId, UUID cartItemId, UUID userId) {
		Cart cart = cartRepository.findActiveCartById(cartId)
			.orElseThrow(CartException.NotFound::new);

		CartItem cartItem = cart.getCartItems().stream()
			.filter(item -> item.getCartItemId().equals(cartItemId))
			.findFirst()
			.orElseThrow(CartItemException.NotFoundInCart::new);

		cartItem.delete(userId);
	}

	@Transactional
	public void deleteCart(UUID cartId, UUID userId) {
		Cart cart = cartRepository.findActiveCartById(cartId)
			.orElseThrow(CartException.NotFound::new);

		cart.delete(userId);
	}

	private CartItem createCartItem(Cart cart, CartItemCreateRequestDto requestDto) {
		ProductResponse.Get product = getProduct(requestDto.getProductId());
		if(product.getQuantity() < requestDto.getQuantity()) {
			throw new CartItemException.NotEnoughStock();
		}

		// 중복된 상품이 있는지 확인하고 중복된 상품이 있다면 수량과 가격만 증가시키기
		if(isDuplicatedCartItem(cart, requestDto.getProductId())) {
			CartItem cartItem = cart.getCartItems().stream()
				.filter(item -> item.getProductId().equals(requestDto.getProductId()))
				.findFirst()
				.orElseThrow(CartItemException.NotFoundInCart::new);

			cartItem.updateQuantityAndPrice(cartItem.getQuantity() + requestDto.getQuantity());
			return cartItem;
		}

		return CartItem.createCartItem(
			cart,
			product.getStoreId(),
			requestDto.getProductId(),
			product.getName(),
			product.getImageUrl(),
			requestDto.getQuantity(),
			requestDto.getQuantity() * product.getPrice()
		);
	}

	private ProductResponse.Get getProduct(UUID productId) {
		try {
			CommonResponse<?> response = productClient.getProduct(productId);
			if (response.getCode().equals(FAIL_CODE)) {
				throw new CartItemException.NotEnoughStock();
			}
			return (ProductResponse.Get)response.getData();
		} catch (JsonProcessingException e) {
			throw new CartItemException.ProductClientError();
		}
	}

	private boolean isDuplicatedCartItem(Cart cart, UUID productId) {
		return cart.getCartItems().stream()
			.anyMatch(cartItem -> cartItem.getProductId().equals(productId));
	}
}
