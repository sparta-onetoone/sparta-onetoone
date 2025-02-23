package com.eureka.spartaonetoone.cart.presentation;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eureka.spartaonetoone.cart.presentation.CartController;
import com.eureka.spartaonetoone.common.config.SecurityConfig;
import com.eureka.spartaonetoone.cart.application.CartService;
import com.eureka.spartaonetoone.cart.application.dtos.request.CartCreateRequestDto;
import com.eureka.spartaonetoone.cart.domain.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = CartController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
	}
)
class CartControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CartService cartService;

	// TODO : Spring Security 관련 설정 추가
	@DisplayName("사용자 id를 통해 장바구니 생성 요청을 한다.")
	@Test
	void cart_save_request_test() throws Exception {
		// Given
		UUID userId = UUID.randomUUID();
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
}