package com.eureka.spartaonetoone.product.integration;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.product.application.ProductService;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductCreateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductSearchRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.response.ProductGetResponseDto;
import com.eureka.spartaonetoone.product.application.exception.ProductException;
import com.eureka.spartaonetoone.product.domain.Product;
import com.eureka.spartaonetoone.product.domain.repository.ProductRepository;
import com.eureka.spartaonetoone.product.infrastructure.ProductRepositoryImpl;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductIntegrationTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    private UUID savedProductId = null;
    private ProductRepositoryImpl productRepositoryImpl;

    @AfterAll
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("신규 상품 생성")
    @MockUser
    void test1() {

        // given
        UUID storeId = UUID.randomUUID();
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(storeId)
                .price(50000)
                .imageUrl("예시이미지")
                .name("예시이름")
                .description("예시설명")
                .quantity(10)
                .build();

        Product product = Product.createProduct(storeId, request.getName(),
                request.getImageUrl(), request.getDescription(),
                request.getPrice(), request.getQuantity());

        // when
        savedProductId = productService.saveProduct(request);

        assertThat(savedProductId).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("등록된 상품 조회")
    @MockUser
    void test2() {

        // when
        Product product = productRepository.findById(savedProductId).orElseThrow(ProductException.ProductNotFoundException::new);

        // then
        assertThat(product.getId()).isEqualTo(savedProductId);
    }

    @Test
    @Order(3)
    @DisplayName("상품 여러 개 생성")
    @MockUser
    void test3() {
        // given
        for (int i = 0; i < 15; i++) {
            ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                    .storeId(UUID.randomUUID())
                    .price(50000)
                    .imageUrl("예시이미지")
                    .name("예시이름2" + i)
                    .description("예시설명")
                    .quantity(10)
                    .build();
            productService.saveProduct(request);
        }
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ProductGetResponseDto> products = productService.getProducts(pageable);

        assertThat(products).isNotNull();
        assertThat(products.getContent().size()).isEqualTo(10);
    }

    @Test
    @Order(4)
    @DisplayName("상품 검색")
    @MockUser
    void test4() {
        // given
        List<ProductCreateRequestDto> requestDtoList = List.of(ProductCreateRequestDto.builder()
                .name("검색용상품1")
                .price(5000)
                .quantity(10)
                .description("예시")
                .storeId(UUID.randomUUID())
                .imageUrl("예시")
                .build(), ProductCreateRequestDto.builder()
                .name("검색용상품2")
                .price(5000)
                .quantity(10)
                .description("예시")
                .storeId(UUID.randomUUID())
                .imageUrl("예시")
                .build()
        );

        ProductSearchRequestDto searchRequest = new ProductSearchRequestDto();
        searchRequest.setName("검색용");
        Pageable pageable = PageRequest.of(0, 10);


        // when
        requestDtoList.forEach(productService::saveProduct);
        Page<ProductGetResponseDto> productGetResponseDtos = productService.searchProducts(searchRequest, pageable);

        // then
        assertThat(productGetResponseDtos).isNotNull();
        assertThat(productGetResponseDtos.getContent().size()).isEqualTo(requestDtoList.size());
        assertThat(productGetResponseDtos.getContent().stream().map(ProductGetResponseDto::getName))
                .anyMatch(result -> result.contains(searchRequest.getName()));

    }

    @Order(5)
    @Test
    @DisplayName("상품 수정")
    @MockUser
    void test5() {
        // when
        Product product = productRepository.findById(savedProductId).orElseThrow(ProductException.ProductNotFoundException::new);
        product.updateQuantity(50);
        product.updateDescription("수정 후 상품");

        productRepository.saveAndFlush(product);
        ProductGetResponseDto response = productService.getProduct(savedProductId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getQuantity()).isEqualTo(50);
        assertThat(response.getDescription()).isEqualTo("수정 후 상품");
    }

    @Order(6)
    @Test
    @DisplayName("상품 삭제")
    @MockUser
    void test6() {
        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // when
        Product product = productRepository.findById(savedProductId).orElseThrow(ProductException.ProductNotFoundException::new);
        product.deleteProduct(userDetails.getUserId());
        productRepository.saveAndFlush(product);
        Product deletedProduct = productRepository.findById(savedProductId).get();

        // then
        assertThat(deletedProduct.getIsDeleted()).isTrue();
    }

}
