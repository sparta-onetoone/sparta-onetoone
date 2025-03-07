package com.eureka.spartaonetoone.order.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.common.client.CartClient;
import com.eureka.spartaonetoone.common.client.PaymentClient;
import com.eureka.spartaonetoone.common.client.ProductClient;
import com.eureka.spartaonetoone.common.dtos.request.PaymentRequest;
import com.eureka.spartaonetoone.common.dtos.request.ProductRequest;
import com.eureka.spartaonetoone.common.dtos.response.CartResponse;
import com.eureka.spartaonetoone.common.dtos.response.ProductResponse;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderCreateRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderSearchRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderSearchResponseDto;
import com.eureka.spartaonetoone.order.application.exceptions.OrderException;
import com.eureka.spartaonetoone.order.domain.Order;
import com.eureka.spartaonetoone.order.domain.OrderItem;
import com.eureka.spartaonetoone.order.domain.OrderStatus;
import com.eureka.spartaonetoone.order.domain.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final static int CANCEL_LIMIT_MINUTES = 5;
	private final static String ROLE_ADMIN = "ROLE_ADMIN";
	private final static String FAIL_CODE = "F000";

	private final CartClient cartClient;
	private final PaymentClient paymentClient;
	private final ProductClient productClient;
	private final OrderRepository orderRepository;

	@Transactional
	public Order saveOrder(OrderCreateRequestDto requestDto) {
		CartResponse.Read cart = cartClient.getCart(requestDto.getCartId());

		if (cart.getCartItems().isEmpty()) {
			throw new OrderException.EmptyCart();
		}

		Order order = Order.createOrder(
			cart.getUserId(),
			cart.getCartItems().get(0).getStoreId(),
			requestDto.getType(),
			requestDto.getRequests()
		);
		orderRepository.save(order);

		validateOrderItem(cart, order);

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
		cartClient.deleteCart(requestDto.getCartId(), cart.getUserId());

		return order;
	}

	@Transactional
	public void requestOrder(UUID orderId) {
		Order order = orderRepository.findActiveOrderById(orderId)
			.orElseThrow(OrderException.NotFound::new);

		if(!order.getStatus().equals(OrderStatus.PENDING)) {
			throw new OrderException.NotPendingStatus();
		}

		orderPaymentRequest(order);
		productQuantityReduceRequest(order);
		order.confirm();
	}

	@Transactional(readOnly = true)
	public OrderSearchResponseDto getOrder(String userRole, UUID orderId) {
		Order order;

		if(userRole.equals(ROLE_ADMIN)) {
			order = orderRepository.findById(orderId)
				.orElseThrow(OrderException.NotFound::new);
		} else {
			order = orderRepository.findActiveOrderById(orderId)
				.orElseThrow(OrderException.NotFound::new);
		}
		return OrderSearchResponseDto.from(order);
	}

	@Transactional(readOnly = true)
	public List<OrderSearchResponseDto> getOrdersByStore(UUID storeId) {
		List<Order> orders = orderRepository.findAllActiveOrderByStoreId(storeId);

		return orders.stream()
			.map(OrderSearchResponseDto::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public Page<OrderSearchResponseDto> getOrders(String userRole, OrderSearchRequestDto requestDto, Pageable pageable) {
		Page<Order> orders = orderRepository
			.searchOrders(userRole, requestDto.getStoreId(), requestDto.getUserId(), pageable);

		return orders.map(OrderSearchResponseDto::from);
	}

	@Transactional
	public void cancelOrder(UUID orderId, UUID storeId) {
		Order order = orderRepository.findActiveOrderById(orderId)
			.orElseThrow(OrderException.NotFound::new);

		if(!order.getStoreId().equals(storeId)) {
			throw new OrderException.CancelPermissionDenied();
		}

		if(!validateOrderCreateTime(order)) {
			throw new OrderException.CancelTimeLimit();
		}

		order.cancel();
	}

	@Transactional
	public void deleteOrder(String userRole, UUID orderId, UUID userId) {
		Order order = orderRepository.findActiveOrderById(orderId)
			.orElseThrow(OrderException.NotFound::new);

		if(userRole.equals("ROLE_CUSTOMER")) {
			if(!order.getUserId().equals(userId)) {
				throw new OrderException.DeletePermissionDenied();
			}
		}
		order.delete(userId);
	}

	private void validateOrderItem(CartResponse.Read cart, Order order) {
		for (CartResponse.CartItemInfo cartItem : cart.getCartItems()) {
			try {
				CommonResponse<?> response = productClient.getProduct(cartItem.getProductId());
				ProductResponse.Get product = (ProductResponse.Get) response.getData();

				if(response.getCode().equals(FAIL_CODE) || product.getQuantity() < cartItem.getQuantity()) {
					order.cancel();
					throw new OrderException.ProductQuantityNotEnough();
				}
			} catch (JsonProcessingException e) {
				throw new OrderException.ProductClientError();
			}
		}
	}

	private void orderPaymentRequest(Order order) {
		PaymentRequest.Create request = new PaymentRequest.Create(order.getOrderId(), order.getTotalPrice());

		CommonResponse<?> response = paymentClient.createPayment(request, order.getUserId());
		if(response.getCode().equals(FAIL_CODE)) {
			order.cancel();
			throw new OrderException.PaymentError();
		}
	}

	private void productQuantityReduceRequest(Order order) {
		try {
			ProductRequest.Reduce request = new ProductRequest.Reduce(
				order.getOrderItems().stream()
					.map(orderItem -> new ProductRequest.Reduce.ReduceProductInfo(
						orderItem.getProductId(),
						orderItem.getQuantity()
					))
					.toList()
			);

			CommonResponse<?> response = productClient.reduceProduct(request);
			if(response.getCode().equals(FAIL_CODE)) {
				order.cancel();
				throw new OrderException.ProductQuantityNotEnough();
			}
		} catch (JsonProcessingException e) {
			throw new OrderException.ProductClientError();
		}
	}

	private boolean validateOrderCreateTime(Order order) {
		return LocalDateTime.now().isBefore(order.getCreatedAt().plusMinutes(CANCEL_LIMIT_MINUTES));
	}
}
