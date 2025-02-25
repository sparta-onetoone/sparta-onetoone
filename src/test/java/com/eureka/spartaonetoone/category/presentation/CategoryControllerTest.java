package com.eureka.spartaonetoone.category.presentation;

import com.eureka.spartaonetoone.mock.MockUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    // 서비스 목(mock) 객체 주o, OrOR,
//    @Autowired
//    private com.eureka.spartaonetoone.category.application.CategoryService categoryService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("카테고리 생성 테스트")
    @Test
    @MockUser
    void create_category_test() throws Exception {
        String name = "한식";
        mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.message").value("카테고리 생성 성공"));
    }

    @DisplayName("카테고리 수정 테스트")
    @Test
    @MockUser
    void update_category_test() throws Exception {
        // 먼저 카테고리 생성
        String originalName = "중식";
        String updatedName = "일식";
        String createResponse = mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .param("name", originalName))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UUID categoryId = UUID.fromString(objectMapper.readTree(createResponse).path("data").path("id").asText());

        mockMvc.perform(put("/api/v1/categories/{categoryId}", categoryId)
                        .with(csrf())
                        .param("name", updatedName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(updatedName))
                .andExpect(jsonPath("$.message").value("카테고리 수정 성공"));
    }

    @DisplayName("카테고리 필터 조회 테스트")
    @Test
    @MockUser
    void filter_categories_test() throws Exception {
        // 먼저 두 개의 카테고리 생성: "한식", "중식"
        mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .param("name", "한식"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .param("name", "중식"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/categories/filter")
                        .param("name", "식"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.message").value("필터 카테고리 조회 성공"));
    }

    @DisplayName("카테고리 단건 조회 테스트")
    @Test
    @MockUser
    @Transactional
    void get_category_by_id_test() throws Exception {
        String name = "양식";
        String createResponse = mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .param("name", name))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UUID categoryId = UUID.fromString(objectMapper.readTree(createResponse).path("data").path("id").asText());

        mockMvc.perform(get("/api/v1/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.message").value("카테고리 조회 성공"));
    }

    @DisplayName("전체 카테고리 조회 테스트")
    @Test
    @MockUser
    void get_all_categories_test() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("전체 카테고리 조회 성공"));
    }

    @DisplayName("카테고리 검색 테스트")
    @Test
    @MockUser
    void search_categories_test() throws Exception {
        // 먼저 여러 카테고리 생성
        String response1 = mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .param("name", "분식"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UUID id1 = UUID.fromString(objectMapper.readTree(response1).path("data").path("id").asText());

        String response2 = mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .param("name", "패스트푸드"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UUID id2 = UUID.fromString(objectMapper.readTree(response2).path("data").path("id").asText());

        List<UUID> ids = List.of(id1, id2);
        String requestJson = objectMapper.writeValueAsString(ids);

        mockMvc.perform(post("/api/v1/categories/search")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.message").value("카테고리 검색 성공"));
    }
}
