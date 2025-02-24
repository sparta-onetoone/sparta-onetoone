package com.eureka.spartaonetoone.product.application;

import com.eureka.spartaonetoone.product.application.dtos.request.ProductCreateRequestDto;
import com.eureka.spartaonetoone.product.application.exception.ProductException;
import com.eureka.spartaonetoone.product.domain.Product;
import com.eureka.spartaonetoone.product.domain.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품 생성 성공")
    @Test
    void saveProduct() {

        // given
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(UUID.randomUUID())
                .price(5000)
                .name("예시상품")
                .description("예시설명")
                .imageUrl("예시이미지")
                .quantity(10)
                .build();

        // when
        UUID savedProductId = productService.saveProduct(request);

        // then
        assertThat(savedProductId).isNotNull();
    }

    @DisplayName("상품 생성 실패 -> 동일한 상점에서 동일한 상품명으로 상품 생성 시도")
    @Test
    void saveProductFailed() {
        //given
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(UUID.randomUUID())
                .price(5000)
                .name("예시상품")
                .description("예시설명")
                .imageUrl("예시이미지")
                .quantity(10)
                .build();

        //when
        productService.saveProduct(request);

        //then
        Assertions.assertThatThrownBy(() -> productService.saveProduct(request))
                .isInstanceOf(ProductException.ProductAlreadyExistsException.class);

    }

    @DisplayName("상품 조회 성공")
    @Test
    void getProduct() {

        //given
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(UUID.randomUUID())
                .price(5000)
                .name("예시상품")
                .description("예시설명")
                .imageUrl("예시이미지")
                .quantity(10)
                .build();

        //when
        UUID saveProductId = productService.saveProduct(request);
        Product product = productRepository.findById(saveProductId).get();

        //then
        assertThat(product.getId()).isEqualTo(saveProductId);
    }

    @DisplayName("상품 조회 실패")
    @Test
    void getProductFailed() {

        //given 
        // 존재하지 않는 상품의 id를 생성
        UUID productId = UUID.randomUUID();

        //when - then
        assertThatThrownBy(() -> productService.getProduct(productId))
                .isInstanceOf(ProductException.ProductNotFoundException.class);
    }

    @DisplayName("상품 수정 성공")
    @Test
    void updateProduct() {

        //given
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(UUID.randomUUID())
                .price(5000)
                .name("예시상품")
                .description("예시설명")
                .imageUrl("예시이미지")
                .quantity(10)
                .build();

        //when
        UUID savedProductId = productService.saveProduct(request);

        Product product = productRepository.findById(savedProductId).get();
        product.updateQuantity(50);
        productRepository.saveAndFlush(product);
        Product updatedProduct = productRepository.findById(savedProductId).get();

        //then
        assertThat(updatedProduct.getQuantity()).isEqualTo(50);


    }

    @DisplayName("상품 삭제 성공")
    @Test
    void deleteProduct() {
        //given
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .storeId(UUID.randomUUID())
                .price(5000)
                .name("예시상품")
                .description("예시설명")
                .imageUrl("예시이미지")
                .quantity(10)
                .build();

        //when
        UUID saveProductId = productService.saveProduct(request);
        Product product = productRepository.findById(saveProductId).get();
        product.deleteProduct();
        productRepository.saveAndFlush(product);
        Product deletedProduct = productRepository.findById(saveProductId).get();

        //then
        assertThat(deletedProduct.getIsDeleted()).isTrue();

    }


}