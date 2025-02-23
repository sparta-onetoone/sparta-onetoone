package com.eureka.spartaonetoone.order.presentation;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.order.application.OrderService;
import com.eureka.spartaonetoone.order.application.dtos.request.OrderCreateRequestDto;
import com.eureka.spartaonetoone.order.application.dtos.response.OrderSearchResponseDto;
import com.eureka.spartaonetoone.order.domain.Order;
import com.eureka.spartaonetoone.order.domain.OrderType;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
class OrderControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private OrderService orderService;

	private UUID orderId;
	private UUID cartId;
	private UUID storeId;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();

		cartId = UUID.randomUUID();
		orderId = UUID.randomUUID();
		storeId = UUID.randomUUID();
	}

	@DisplayName("인증된 사용자는 주문을 생성할 수 있다.")
	@Test
	@MockUser(role = UserRole.CUSTOMER)
	void save_order_test() throws Exception {
		User user = getUser();
		UUID cartId = UUID.randomUUID();
		OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
			.cartId(cartId)
			.type(OrderType.DELIVERY)
			.requests("요청 사항")
			.build();
		Order createdOrder = createMockOrder(user.getUserId());

		when(orderService.saveOrder(any(OrderCreateRequestDto.class))).thenReturn(createdOrder);

		mockMvc.perform(post("/api/v1/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").exists())
			.andExpect(jsonPath("$.message").value("주문 생성 성공"));
	}

	@DisplayName("관리자는 주문을 생성할 수 없다.")
	@Test
	@MockUser(role = UserRole.ADMIN)
	void save_order_with_invalid_role_exception_test() throws Exception {
		User user = getUser();
		UUID cartId = UUID.randomUUID();
		OrderCreateRequestDto requestDto = OrderCreateRequestDto.builder()
			.cartId(cartId)
			.type(OrderType.DELIVERY)
			.requests("요청 사항")
			.build();
		Order createdOrder = createMockOrder(user.getUserId());

		when(orderService.saveOrder(any(OrderCreateRequestDto.class))).thenReturn(createdOrder);

		mockMvc.perform(post("/api/v1/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().is4xxClientError());
	}

	@DisplayName("사용자는 주문 id로 주문을 조회할 수 있다.")
	@Test
	@MockUser(role = UserRole.CUSTOMER)
	void get_order_by_id_test() throws Exception {
		// given
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		User user = userDetails.getUser();

		Order order = createMockOrder(user.getUserId());

		// when
		when(orderService.getOrder(user.getRole().toString(), orderId))
			.thenReturn(OrderSearchResponseDto.from(order));

		mockMvc.perform(get("/api/v1/orders/{order_id}", orderId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("주문 조회 성공"));
	}

	@DisplayName("가게 주인은 자신의 가게 주문 목록을 조회할 수 있다.")
	@Test
	@MockUser(role = UserRole.OWNER)
	void get_orders_for_store_owner_test() throws Exception {
		// given
		List<OrderSearchResponseDto> orderList = Arrays.asList(
			OrderSearchResponseDto.from(createMockOrder(UUID.randomUUID())),
			OrderSearchResponseDto.from(createMockOrder(UUID.randomUUID()))
		);

		// when
		when(orderService.getOrdersByStore(any(UUID.class))).thenReturn(orderList);

		// then
		mockMvc.perform(get("/api/v1/orders")
				.param("store_id", storeId.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.message").value("가게 주문 목록 조회 성공"));
	}

	private User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		return userDetails.getUser();
	}

	private Order createMockOrder(UUID userId) {
		return Order.createOrder(userId, cartId, OrderType.DELIVERY, "요청 사항");
	}
}