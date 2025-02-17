package com.eureka.spartaonetoone.domain.cart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.domain.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.domain.cart.domain.Cart;
import com.eureka.spartaonetoone.domain.cart.domain.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;

	@Transactional
	public Cart saveCart(CartCreateRequestDto requestDto) {
		Cart cart = Cart.of(requestDto.getUserId());
		return cartRepository.save(cart);
	}
}
