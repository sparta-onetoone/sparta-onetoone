package com.eureka.spartaonetoone.store.presentation.controller;

import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
// @AutoConfigureMockMvc
public class StoreControllerTest {

	// @Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.build();
	}

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	// @WithMockUser(roles = "OWNER")
	@MockUser
	public void testCreateStore() throws Exception {
		UUID categoryId = UUID.randomUUID();  // Simulating category selection
		StoreRequestDto requestDto = new StoreRequestDto(
			UUID.randomUUID(), "Test Store", "OPEN", "010-1234-5678",
			"Description here", 1000, 50, 5.0f, 0, "testuser", categoryId);

		mockMvc.perform(post("/api/v1/stores")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print());
			// .andExpect(status().isOk())
			// .andExpect(jsonPath("$.name")
			// 	.value("Test Store"));
	}

	@Test
	@WithMockUser(roles = {"CUSTOMER", "ADMIN"})
	public void testGetStore() throws Exception {
		UUID storeId = UUID.randomUUID(); // This would typically be set up or mocked

		mockMvc.perform(get("/api/v1/stores/" + storeId)
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = {"CUSTOMER", "OWNER", "ADMIN"})
	public void testGetAllStores() throws Exception {
		mockMvc.perform(get("/api/v1/stores")
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "owner", roles = "OWNER")
	public void testUpdateStore() throws Exception {
		UUID storeId = UUID.randomUUID();
		StoreRequestDto requestDto = new StoreRequestDto(
			UUID.fromString("owner"), "수정된 가게", "CLOSED", "010-3333-4444",
			"수정된 설명", 1200, 300, 4.5f, 10, "수정자", UUID.randomUUID());

		mockMvc.perform(put("/api/v1/stores/" + storeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))
				.with(csrf()))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteStore() throws Exception {
		UUID storeId = UUID.randomUUID();

		mockMvc.perform(delete("/api/v1/stores/" + storeId)
				.with(csrf()))
			.andExpect(status().isNoContent());
	}
}
