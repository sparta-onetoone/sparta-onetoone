package com.eureka.spartaonetoone.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.common.client.CartClient;
import com.eureka.spartaonetoone.common.client.ProductClient;
import com.eureka.spartaonetoone.common.dtos.response.CartResponse;
import com.eureka.spartaonetoone.common.dtos.response.ProductResponse;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderCreateRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderSearchRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderSearchResponseDto;
import com.eureka.spartaonetoone.order.application.exceptions.OrderException;
import com.eureka.spartaonetoone.order.domain.Order;
import com.eureka.spartaonetoone.order.domain.OrderType;
import com.eureka.spartaonetoone.order.domain.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private ProductClient productClient;

	@Mock
	private CartClient cartClient;

	@Mock
	private OrderRepository orderRepository;

	private UUID userId;
	private UUID orderId;
	private UUID cartId;
	private UUID productId;
	private UUID storeId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		orderId = UUID.randomUUID();
		cartId = UUID.randomUUID();
		productId = UUID.randomUUID();
		storeId = UUID.randomUUID();
	}

	@Transactional
	@DisplayName("유효한 장바구니로 주문 생성 시 주문이 성공적으로 저장되어야 한다")
	@Test
	void save_order_with_valid_cart_test() throws JsonProcessingException {
		// given
		OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
			.cartId(cartId)
			.type(OrderType.DELIVERY)
			.requests("요청 사항")
			.build();

		doReturn(CommonResponse.success(createMockProduct(), null))
			.when(productClient).getProduct(any(UUID.class));
		when(cartClient.getCart(any(UUID.class))).thenReturn(createMockCart());
		when(orderRepository.save(any(Order.class))).thenReturn(
			Order.createOrder(userId, UUID.randomUUID(), OrderType.DELIVERY, "요청 사항")
		);

		// when
		Order result = orderService.saveOrder(requestDto);

		// then
		assertThat(result).isNotNull();
		verify(orderRepository).save(any(Order.class));
	}

	@Test
	@DisplayName("빈 장바구니로 주문 생성 시 예외가 발생해야 한다")
	void save_order_with_invalid_cart_exception_test() {
		// given
		OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
			.cartId(cartId)
			.type(OrderType.DELIVERY)
			.requests("요청 사항")
			.build();
		CartResponse.Read emptyCart = new CartResponse.Read(userId, UUID.randomUUID(), List.of());
		when(cartClient.getCart(cartId)).thenReturn(emptyCart);

		// when & then
		assertThatThrownBy(() -> orderService.saveOrder(requestDto))
			.isInstanceOf(OrderException.EmptyCart.class);
	}

	@Test
	@DisplayName("관리자로 주문 조회 시 주문이 반환되어야 한다")
	void get_order_as_admin_test() {
		// given
		Order mockOrder = createMockOrder();
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

		// when
		OrderSearchResponseDto result = orderService.getOrder("ROLE_ADMIN", orderId);

		// then
		assertThat(result).isNotNull();
		verify(orderRepository).findById(orderId);
	}

	@Test
	@DisplayName("사용자 ID로 주문 목록 조회 시 주문 목록이 반환되어야 한다")
	void get_orders_by_user_id_test() {
		// given
		Page<Order> mockOrders = new PageImpl<>(Arrays.asList(createMockOrder(), createMockOrder()));
		String userRole = "ROLE_CUSTOMER";
		OrderSearchRequestDto requestDto = OrderSearchRequestDto.builder()
			.storeId(storeId)
			.userId(userId)
			.build();

		when(orderRepository.searchOrders(userRole, storeId, userId, null)).thenReturn(mockOrders);

		// when
		Page<OrderSearchResponseDto> result = orderService.getOrders(userRole, requestDto, null);

		// then
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("권한이 있는 고객이 주문 삭제 시 주문이 삭제되어야 한다")
	void deleteOrder_AsCustomerWithPermission_ShouldDeleteOrder() {
		// given
		Order mockOrder = createMockOrder();
		when(orderRepository.findActiveOrderById(orderId)).thenReturn(Optional.of(mockOrder));

		// when
		orderService.deleteOrder("ROLE_CUSTOMER", orderId, userId);

		// then
		verify(orderRepository).findActiveOrderById(orderId);
	}

	@Test
	@DisplayName("권한이 없는 고객이 주문 삭제 시 예외가 발생해야 한다")
	void deleteOrder_AsCustomerWithoutPermission_ShouldThrowException() {
		// given
		Order mockOrder = createInvalidMockOrder();
		when(orderRepository.findActiveOrderById(orderId)).thenReturn(Optional.of(mockOrder));

		// when & then
		assertThatThrownBy(() -> orderService.deleteOrder("ROLE_CUSTOMER", orderId, userId))
			.isInstanceOf(OrderException.DeletePermissionDenied.class);
	}

	private CartResponse.Read createMockCart() {
		CartResponse.CartItemInfo item = new CartResponse.CartItemInfo(
			UUID.randomUUID(), productId, "상품 이름", "상품 이미지", 1, 1000
		);
		return new CartResponse.Read(cartId, userId, List.of(item));
	}

	private ProductResponse.Get createMockProduct() {
		return new ProductResponse.Get("상품 이름", "상품 이미지", 1, 1000, storeId);
	}

	private Order createMockOrder() {
		return Order.createOrder(userId, cartId, OrderType.DELIVERY, "요청 사항");
	}

	private Order createInvalidMockOrder() {
		return Order.createOrder(UUID.randomUUID(), cartId, OrderType.DELIVERY, "요청 사항");
	}
}