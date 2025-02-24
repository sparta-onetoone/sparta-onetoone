package com.eureka.spartaonetoone.product.presentation;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.product.application.ProductService;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductCreateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductUpdateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.response.ProductGetResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProductControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired
    private ProductService productService;


    private static List<ProductCreateRequestDto> productCreateRequestDtos() {
        return List.of(
                ProductCreateRequestDto.builder()
                        .storeId(UUID.randomUUID())
                        .price(5000)
                        .name("예시상품")
                        .description("예시설명")
                        .imageUrl("예시이미지")
                        .quantity(0)
                        .build(),
                ProductCreateRequestDto.builder()
                        .storeId(UUID.randomUUID())
                        .price(0)
                        .name("예시상품")
                        .description("예시설명")
                        .imageUrl("예시이미지")
                        .quantity(10)
                        .build(),
                ProductCreateRequestDto.builder()
                        .storeId(UUID.randomUUID())
                        .price(0)
                        .name("")
                        .description("예시설명")
                        .imageUrl("예시이미지")
                        .quantity(10)
                        .build()

        );
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("상품 생성")
    @Test
    @MockUser
    void saveProduct() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(storeId)
                .price(5000)
                .name("예시상품")
                .description("예시설명")
                .imageUrl("예시이미지")
                .quantity(10)
                .build();


        // when - then
        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("상품이 성공적으로 생성되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.code").value("S000"));


    }

    @DisplayName("상품 생성 실패 -> 1. 상품 수량 오류, 2. 상품 가격 오류, 3. 상품명 누락")
    @ParameterizedTest
    @MethodSource("productCreateRequestDtos")
    @MockUser
    void saveProductFailed(ProductCreateRequestDto request) throws Exception {
        // given -> @MethodSource로 주입받는다.

        // when - then
        mockMvc.perform(post("/api/v1/products")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("상품 조회 성공")
    @Test
    @MockUser
    void getProduct() throws Exception {

        // given
        UUID storeId = UUID.randomUUID();
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(storeId)
                .price(5000)
                .name("예시상품")
                .description("예시설명")
                .imageUrl("예시이미지")
                .quantity(10)
                .build();


        // when - then
        UUID saveProductId = productService.saveProduct(request);

        mockMvc.perform(get("/api/v1/products/{productId}", saveProductId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 조회되었습니다."))
                .andExpect(jsonPath("$.code").value("S000"));
    }

    @DisplayName("상품 조회 실패")
    @Test
    @MockUser
    void getProductFailed() throws Exception {

        // given
        UUID nonExistentProductId = UUID.randomUUID();

        // when - then
        mockMvc.perform(get("/api/v1/products/{productId}", nonExistentProductId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("상품 수정 성공")
    @Test
    @MockUser
    void updateProduct() throws Exception {

        //given
        ProductCreateRequestDto createRequest = ProductCreateRequestDto.builder()
                .price(2000)
                .storeId(UUID.randomUUID())
                .imageUrl("기본이미지")
                .name("기본이름")
                .description("기본설명")
                .quantity(30)
                .build();

        ProductUpdateRequestDto updateRequest = ProductUpdateRequestDto.builder()
                .price(5000)
                .name("수정후이름")
                .description("수정후설명")
                .quantity(50)
                .build();

        //when - then
        UUID savedProductId = productService.saveProduct(createRequest);
        ProductGetResponseDto getResponseDto = productService.getProduct(savedProductId);
        Assertions.assertThat(getResponseDto.getName()).isEqualTo(createRequest.getName());
        Assertions.assertThat(getResponseDto.getImageUrl()).isEqualTo(createRequest.getImageUrl());
        Assertions.assertThat(getResponseDto.getDescription()).isEqualTo(createRequest.getDescription());
        Assertions.assertThat(getResponseDto.getQuantity()).isEqualTo(createRequest.getQuantity());

        mockMvc.perform(put("/api/v1/products/{productId}", savedProductId)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 수정되었습니다."));

    }

    @DisplayName("상품 수정 실패")
    @Test
    @MockUser
    void updateProductFailed() throws Exception {

        //given
        ProductCreateRequestDto createRequest = ProductCreateRequestDto.builder()
                .price(2000)
                .storeId(UUID.randomUUID())
                .imageUrl("기본이미지")
                .name("기본이름")
                .description("기본설명")
                .quantity(30)
                .build();

        ProductUpdateRequestDto updateRequest = ProductUpdateRequestDto.builder()
                .price(0)
                .name(null)
                .description("수정후설명")
                .quantity(50)
                .build();

        //when - then
        UUID savedProductId = productService.saveProduct(createRequest);
        ProductGetResponseDto getResponseDto = productService.getProduct(savedProductId);


        mockMvc.perform(put("/api/v1/products/{productId}", savedProductId)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("상품 검색")
    @MockUser
    @Test
    void searchProduct() throws Exception {
        // given
        ProductCreateRequestDto createRequest = ProductCreateRequestDto.builder()
                .price(2000)
                .storeId(UUID.randomUUID())
                .imageUrl("기본이미지")
                .name("정확히 이렇게 생긴 걸 찾아요")
                .description("정확히 이런 설명입니다")
                .quantity(30)
                .build();


        // when
        UUID saveProductId = productService.saveProduct(createRequest);
        ProductGetResponseDto product = productService.getProduct(saveProductId);

        // then
        mockMvc.perform(get("/api/v1/products/search")
                        .param("name", "정확히 이렇게 생긴 걸 찾아요")
                        .param("description", "정확히 이런 설명입니다"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("정확히 이렇게 생긴 걸 찾아요"))
                .andExpect(jsonPath("$.data.content[0].description").value("정확히 이런 설명입니다"));


    }

}