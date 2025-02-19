package com.eureka.spartaonetoone.domain.product.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.product.application.ProductService;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductCreateRequestDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductGetResponseDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductSearchRequestDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody @Valid ProductCreateRequestDto request
    ) {
        UUID savedProductId = productService.saveProduct(request);
        return ResponseEntity.ok(CommonResponse.success(savedProductId, "상품이 성공적으로 생성되었습니다."));
    }

    @GetMapping("{product_id}")
    public ResponseEntity<?> getProduct(@PathVariable(name = "product_id") UUID productId) {
        ProductGetResponseDto product = productService.getProduct(productId);
        return ResponseEntity.ok(CommonResponse.success(product, "상품이 성공적으로 조회되었습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(name = "page", defaultValue = "1") int page,
                                         @RequestParam(name = "limit", defaultValue = "10") int limit) {
        Pageable pageable = getPageable(page, limit);
        Page<ProductGetResponseDto> products = productService.getProducts(pageable);
        return ResponseEntity.ok(CommonResponse.success(products, "상품목록이 성공적으로 조회되었습니다."));
    }

    @PutMapping("{product_id}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "product_id") UUID productId,
                                           @RequestBody @Valid ProductUpdateRequestDto request) {
        UUID updatedProductId = productService.updateProduct(productId, request);
        return ResponseEntity.ok(CommonResponse.success(updatedProductId, "상품이 성공적으로 수정되었습니다."));

    }

    @DeleteMapping("{product_id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "product_id") UUID productId) {
        UUID deletedProductId = productService.deleteProduct(productId);
        return ResponseEntity.ok(CommonResponse.success(deletedProductId, "상품이 성공적으로 삭제되었습니다."));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(ProductSearchRequestDto request,
                                            @RequestParam(name = "page", defaultValue = "1") int page,
                                            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        Pageable pageable = getPageable(page, limit);
        Page<ProductGetResponseDto> products = productService.searchProducts(request, pageable);
        return ResponseEntity.ok(CommonResponse.success(products, "상품목록 검색결과가 성공적으로 조회되었습니다."));
    }

    private Pageable getPageable(int page, int limit) {
        return PageRequest.of(page - 1, limit);

    }
}
