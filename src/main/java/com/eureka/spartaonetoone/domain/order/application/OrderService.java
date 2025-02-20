package com.eureka.spartaonetoone.domain.order.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.common.client.CartClient;
import com.eureka.spartaonetoone.common.dtos.CartResponse;
import com.eureka.spartaonetoone.domain.order.application.dtos.request.OrderCreateRequestDto;
import com.eureka.spartaonetoone.domain.order.application.exceptions.OrderException;
import com.eureka.spartaonetoone.domain.order.domain.Order;
import com.eureka.spartaonetoone.domain.order.domain.OrderItem;
import com.eureka.spartaonetoone.domain.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final CartClient cartClient;
	private final OrderRepository orderRepository;

	@Transactional
	public Order saveOrder(OrderCreateRequestDto requestDto) {
		CartResponse.Read cart = cartClient.getCart(requestDto.getCartId());

		if (cart.getCartItems().isEmpty()) {
			throw new OrderException.EmptyCart();
		}

		Order order = Order.createOrder(
			cart.getUserId(),
			cart.getCartItems().get(0).getStoreId(), // TODO : cart 에 storeId 추가
			requestDto.getType(),
			requestDto.getRequests()
		);
		orderRepository.save(order);

		cart.getCartItems().forEach(cartItem ->
			order.addOrderItem(
				OrderItem.createOrderItem(
					order,
					cartItem.getProductId(),
					cartItem.getProductName(),
					cartItem.getProductImage(),
					cartItem.getQuantity(),
					cartItem.getPrice()
				)
			)
		);
		order.calculateTotalPrice();

		return order;
	}
}
