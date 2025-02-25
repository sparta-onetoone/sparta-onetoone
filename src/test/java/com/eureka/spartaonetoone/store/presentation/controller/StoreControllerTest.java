//package com.eureka.spartaonetoone.store.presentation.controller;
//
//import com.eureka.spartaonetoone.category.domain.Category;
//import com.eureka.spartaonetoone.mock.MockUser;
//import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
//import com.eureka.spartaonetoone.user.domain.UserRole;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.UUID;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class StoreControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
////    // @MockitoBean를 사용하여 StoreService의 목 객체를 주입할 수 있음(필요시)
////    @MockitoBean
////    private StoreService storeService;
////
////    @MockitoBean
////    private CategoryClient categoryClient;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//    }
//
//    @DisplayName("OWNER는 가게를 생성할 수 있다.")
//    @Test
//    @MockUser(role = UserRole.OWNER)
//    void create_store_test() throws Exception {
//        // given: 소유자 userId를 생성하여 요청 DTO에 적용
//        List<String> categoryIds = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());
//        UUID userId = UUID.randomUUID();
//        StoreRequestDto requestDto = new StoreRequestDto(
//                userId, "Test Store", "OPEN", "010-1234-5678",
//                "Description here", 1000, 50, 5.0f, 0, "testuser", categoryIds
//        );
//
//        // when & then
//        mockMvc.perform(post("/api/v1/stores")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("가게 등록 성공"));
//    }
//
//    @DisplayName("특정 가게 조회 테스트")
//    @Test
//    @WithMockUser(roles = {"OWNER"})
//    void get_store_by_id_test() throws Exception {
//        UUID storeId = UUID.randomUUID();
//
//        mockMvc.perform(get("/api/v1/stores/{store_id}", storeId)
//                        .with(csrf()))
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("전체 가게 목록 조회 테스트")
//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    void get_all_stores_test() throws Exception {
//        mockMvc.perform(get("/api/v1/stores")
//                        .with(csrf()))
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("OWNER는 자신의 가게 정보를 수정할 수 있다.")
//    @Test
//    @WithMockUser(roles = {"OWNER"})
//    void update_store_test() throws Exception {
//        // PreAuthorize 조건에 맞게, 소유자 userId와 가게의 소유자가 일치하도록 설정
//        UUID userId = UUID.randomUUID();
//        // 테스트에서는 가게 id를 userId와 동일하게 하여 '본인 소유' 조건을 만족시킴
//        UUID storeId = userId;
//        List<String> categoryIds = List.of(UUID.randomUUID().toString());
//        StoreRequestDto requestDto = new StoreRequestDto(
//                userId, "Updated Store", "CLOSED", "010-3333-4444",
//                "Updated Description", 1200, 300, 4.5f, 10, "updater", categoryIds
//        );
//
//        mockMvc.perform(put("/api/v1/stores/{store_id}", storeId)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("ADMIN은 모든 가게를 삭제할 수 있다.")
//    @Test
//    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
//    void delete_store_as_admin_test() throws Exception {
//        // 가게 생성을 위한 적절한 매개변수 제공
//        UUID userId = UUID.randomUUID();
//        List<String> categoryIds = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());
//
//        Category category = Category.of("피자");
//        Field idField = Category.class.getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(category, UUID.randomUUID());
//ㅗ
////        when(categoryClient.getCategoryByIds(Mockito.any())).thenReturn(List.of(category));
//
//        StoreRequestDto createStoreDto = new StoreRequestDto(
//                userId, "Store Name", "OPEN", "010-1234-5678",
//                "Description", 1000, 50, 5.0f, 0, "adminUser", categoryIds);
//
//        String storeJson = objectMapper.writeValueAsString(createStoreDto);
//
//        String createStoreResponse = mockMvc.perform(post("/api/v1/stores")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(storeJson))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        TemporaryDto createdStore = objectMapper.readValue(createStoreResponse, TemporaryDto.class);
//        UUID storeId = createdStore.getData().getStoreId();
//
//        // 생성된 가게 삭제
//        mockMvc.perform(delete("/api/v1/stores/{store_id}", storeId)
//                        .with(csrf()))
//                .andExpect(status().isOk());
//    }
////    @DisplayName("ADMIN은 모든 가게를 삭제할 수 있다.")
////    @Test
////    @MockUser(role = UserRole.ADMIN)
////    void delete_store_as_admin_test() throws Exception {
////
////
////        mockMvc.perform(delete("/api/v1/stores/{store_id}", storeId)
////                        .with(csrf()))
////                .andExpect(status().isOk());
////    }
//
//
////    @DisplayName("OWNER는 본인이 소유한 가게만 삭제 가능하다.")
////    @Test
////    @MockUser(role = UserRole.ADMIN)
////    void delete_store_as_owner_fail_test() throws Exception {
////        // 가게 소유자와 테스트 인증 사용자의 ID가 일치하지 않아 삭제 요청이 거부됨
////        UUID storeId = UUID.randomUUID();
////
////        mockMvc.perform(delete("/api/v1/stores/{store_id}", storeId)
////                        .with(csrf()))
////                .andExpect(status().isForbidden());
////    }
////
////    private static class TemporaryDto {
////        private StoreResponseDto data;
////
////        public StoreResponseDto getData() {
////            return data;
////        }
////    }
//
//}
