package com.eureka.spartaonetoone.review.presentation;

import com.eureka.spartaonetoone.review.application.dtos.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class ReviewControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("인증된 사용자는 리뷰를 생성할 수 있다.")
    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testCreateReview() throws Exception {
        UUID orderId = UUID.randomUUID();
        ReviewRequestDto requestDto = new ReviewRequestDto(orderId, 5, "Integration review", "http://img.url");
        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("Integration review"))
                .andExpect(jsonPath("$.message").value("리뷰 생성 성공"));
    }

    @DisplayName("사용자는 리뷰 ID로 리뷰를 조회할 수 있다.")
    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testGetReviewById() throws Exception {
        // 먼저 리뷰 생성
        UUID orderId = UUID.randomUUID();
        ReviewRequestDto createDto = new ReviewRequestDto(orderId, 4, "Review to get", "http://img.get");
        String createJson = objectMapper.writeValueAsString(createDto);

        String createResponse = mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TemporaryDto createdReview = objectMapper.readValue(createResponse, TemporaryDto.class);
        UUID reviewId = createdReview.getData().getReviewId();

        mockMvc.perform(get("/api/v1/reviews/{review_id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviewId").value(reviewId.toString()))
                .andExpect(jsonPath("$.data.content").value("Review to get"));
    }


    @DisplayName("사용자는 리뷰를 수정할 수 있다.")
    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testUpdateReview() throws Exception {
        // 먼저 리뷰 생성
        UUID orderId = UUID.randomUUID();
        ReviewRequestDto createDto = new ReviewRequestDto(orderId, 3, "Old review", "http://old.img");
        String createJson = objectMapper.writeValueAsString(createDto);

        String createResponse = mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TemporaryDto createdReview = objectMapper.readValue(createResponse, TemporaryDto.class);
        UUID reviewId = createdReview.getData().getReviewId();

        // 수정 요청
        ReviewRequestDto updateDto = new ReviewRequestDto(orderId, 5, "Updated review", "http://new.img");
        String updateJson = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/v1/reviews/{review_id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("Updated review"))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.message").value("리뷰 수정 성공"));
    }
    @DisplayName("사용자는 리뷰를 삭제할 수 있다.")
    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testDeleteReview() throws Exception {
        // 먼저 리뷰 생성
        UUID orderId = UUID.randomUUID();
        ReviewRequestDto createDto = new ReviewRequestDto(orderId, 3, "Review to delete", "http://delete.img");
        String createJson = objectMapper.writeValueAsString(createDto);

        String createResponse = mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TemporaryDto createdReview = objectMapper.readValue(createResponse, TemporaryDto.class);
        UUID reviewId = createdReview.getData().getReviewId();

        mockMvc.perform(delete("/api/v1/reviews/{review_id}", reviewId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @DisplayName("사용자는 여러 orderId를 기반으로 리뷰를 검색할 수 있다.")
    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    public void testSearchReviews() throws Exception {
        // 두 개의 리뷰 생성 (서로 다른 orderId 사용)
        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();
        ReviewRequestDto dto1 = new ReviewRequestDto(orderId1, 5, "Review one", "http://img1.url");
        ReviewRequestDto dto2 = new ReviewRequestDto(orderId2, 4, "Review two", "http://img2.url");

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isOk());

        // orderIds 목록으로 검색 요청
        List<UUID> orderIds = List.of(orderId1, orderId2);
        String orderIdsJson = objectMapper.writeValueAsString(orderIds);

        mockMvc.perform(post("/api/v1/reviews/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderIdsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.message").value("리뷰 검색 성공"));
    }

    private static class TemporaryDto {
        private ReviewResponseDto data;

        public ReviewResponseDto getData() {
            return data;
        }
    }
}
