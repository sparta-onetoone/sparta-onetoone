package com.eureka.spartaonetoone.store.presentation.controller;

import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class StoreControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	// given: 유효한 StoreRequestDto 생성, when: 인증 정보를 포함한 POST 요청, then: 200 OK와 응답 내용 검증
	@Test
	@WithMockUser(username = "owner", roles = {"OWNER"})
	public void testCreateAndGetStoreSuccess() throws Exception {
		StoreRequestDto requestDto = new StoreRequestDto(
			UUID.randomUUID(),                           // userId
			"통합 테스트 가게",                           // 가게 이름
			"OPEN",                                      // 가게 상태
			"010-1111-2222",                             // 전화번호
			"통합 테스트 설명",                          // 설명
			1200,                                        // 최소 주문 금액
			300,                                         // 배달비
			0.0f,                                        // 평점 (초기값)
			0,                                           // 리뷰 수 (초기값)
			"생성자",                                    // 생성자
			UUID.randomUUID()                            // categoryId
		);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/v1/stores")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto))
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("통합 테스트 가게"))
			.andDo(print());

		// Additional GET test to fetch the created store can be added here
	}

	// given: 존재하지 않는 storeId 생성, when: GET 요청, then: 404 NOT_FOUND 검증
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void testGetStoreNotFound() throws Exception {
		UUID invalidId = UUID.randomUUID();
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/v1/stores/" + invalidId)
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isNotFound())
			.andDo(print());
	}

	// given: 전체 가게 목록 조회를 위한 GET 요청, when: 요청 수행, then: 200 OK 상태 코드 검증
	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void testGetAllStores() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stores")
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());
	}

	@Test
	@WithMockUser(username = "owner", roles = {"OWNER"})
	public void testUpdateStoreSuccess() throws Exception {
		UUID userId = UUID.randomUUID();
		StoreRequestDto updateDto = new StoreRequestDto(
			userId,
			"수정된 가게",
			"CLOSED",
			"010-9999-8888",
			"수정된 설명",
			1500,
			700,
			0.0f,
			0,
			"수정자",
			UUID.randomUUID()
		);
		UUID storeId = UUID.randomUUID(); // This should ideally be fetched from a setup or a previous test

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/stores/" + storeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());
	}

	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void testDeleteStore() throws Exception {
		UUID storeId = UUID.randomUUID(); // This should ideally be fetched from a setup or a previous test

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/stores/" + storeId)
				.with(csrf()))
			.andExpect(MockMvcResultMatchers.status().isNoContent())
			.andDo(print());
	}
}
