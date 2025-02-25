package com.eureka.spartaonetoone.cart.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
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

import com.eureka.spartaonetoone.cart.application.dtos.response.CartSearchResponseDto;
import com.eureka.spartaonetoone.cart.application.CartService;
import com.eureka.spartaonetoone.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.cart.domain.Cart;
import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
class CartControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CartService cartService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

	@DisplayName("사용자 id를 통해 장바구니 생성 요청을 한다.")
	@Test
	@MockUser(role = UserRole.CUSTOMER)
	void cart_save_request_test() throws Exception {
		// Given
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		UUID userId = userDetails.getUserId();

		CartCreateRequestDto request = CartCreateRequestDto.builder()
			.userId(userId)
			.build();
		Cart savedCart = Cart.createCart(userId);

		when(cartService.saveCart(any(CartCreateRequestDto.class))).thenReturn(savedCart);

		// When, Then
		mockMvc.perform(post("/api/v1/carts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S000"))
			.andExpect(jsonPath("$.data.cart_id").value(savedCart.getCartId()))
			.andExpect(jsonPath("$.message").value("장바구니 생성 성공"));

		verify(cartService).saveCart(any(CartCreateRequestDto.class));
	}

	@DisplayName("장바구니 id를 통해 장바구니를 조회할 수 있다.")
	@Test
	@MockUser(role = UserRole.CUSTOMER)
	void get_cart_test() throws Exception {
		// Given
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		UUID userId = userDetails.getUserId();
		UUID cartId = UUID.randomUUID();

		CartSearchResponseDto responseDto = CartSearchResponseDto.builder()
			.cartId(cartId)
			.userId(userId)
			.cartItems(new ArrayList<>())
			.build();

		when(cartService.getCart(cartId)).thenReturn(responseDto);

		// When & Then
		mockMvc.perform(get("/api/v1/carts/{cart_id}", cartId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S000"))
			.andExpect(jsonPath("$.data").isNotEmpty())
			.andExpect(jsonPath("$.data.user_id").value(userId.toString()))
			.andExpect(jsonPath("$.message").value("장바구니 조회 성공"));
	}
}