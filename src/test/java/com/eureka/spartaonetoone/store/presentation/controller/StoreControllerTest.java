package com.eureka.spartaonetoone.store.presentation.controller;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.store.application.StoreService;
import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class StoreControllerTest {

	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private StoreService storeService;
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();
	}

	@DisplayName("OWNER는 가게를 생성할 수 있다.")
	@Test
	@MockUser(role = UserRole.OWNER)
	public void testCreateStore() throws Exception {
		// given: 카테고리 ID를 리스트로 변경
		List<String> categoryIds = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		StoreRequestDto requestDto = new StoreRequestDto(
				UUID.randomUUID(), "Test Store", "OPEN", "010-1234-5678",
				"Description here", 1000, 50, 5.0f, 0, "testuser", categoryIds
		);

		// when & then
		mockMvc.perform(post("/api/v1/stores")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("가게 등록 성공"));
	}

	@DisplayName("특정 가게 조회 테스트")
	@Test
	@WithMockUser(roles = {"CUSTOMER", "ADMIN"})
	public void testGetStore() throws Exception {
		UUID storeId = UUID.randomUUID();

		mockMvc.perform(get("/api/v1/stores/" + storeId)
						.with(csrf()))
				.andExpect(status().isOk());
	}

	@DisplayName("전체 가게 목록 조회 테스트")
	@Test
	@WithMockUser(roles = {"CUSTOMER", "OWNER", "ADMIN"})
	public void testGetAllStores() throws Exception {
		mockMvc.perform(get("/api/v1/stores")
						.with(csrf()))
				.andExpect(status().isOk());
	}

	@DisplayName("OWNER는 자신의 가게 정보를 수정할 수 있다.")
	@Test
	@WithMockUser(username = "owner", roles = "OWNER")
	public void testUpdateStore() throws Exception {
		UUID storeId = UUID.randomUUID();
		List<String> categoryIds = List.of(UUID.randomUUID().toString());

		StoreRequestDto requestDto = new StoreRequestDto(
				UUID.randomUUID(), "수정된 가게", "CLOSED", "010-3333-4444",
				"수정된 설명", 1200, 300, 4.5f, 10, "수정자", categoryIds
		);

		mockMvc.perform(put("/api/v1/stores/" + storeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
						.with(csrf()))
				.andExpect(status().isOk());
	}

	@DisplayName("ADMIN은 모든 가게를 삭제할 수 있다.")
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteStore_AsAdmin() throws Exception {
		UUID storeId = UUID.randomUUID();

		mockMvc.perform(delete("/api/v1/stores/" + storeId)
						.with(csrf()))
				.andExpect(status().isNoContent());
	}

	@DisplayName("OWNER는 본인이 소유한 가게만 삭제 가능하다.")
	@Test
	@WithMockUser(username = "owner", roles = "OWNER")
	public void testDeleteStore_AsOwner_Fail() throws Exception {
		UUID storeId = UUID.randomUUID();
		UUID otherOwnerId = UUID.randomUUID(); // 다른 사용자의 가게

		// 가게를 생성한 사용자가 아닌 경우 예외 발생
		mockMvc.perform(delete("/api/v1/stores/" + storeId)
						.with(csrf()))
				.andExpect(status().isForbidden()); // 권한이 없을 때 403 반환
	}
}
