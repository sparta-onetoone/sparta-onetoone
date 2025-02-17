package com.eureka.spartaonetoone.domain.product.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.product.application.ProductService;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductCreateRequestDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> saveProduct(@RequestBody @Valid
                                         ProductCreateRequestDto productCreateRequestDto) {
        return ResponseEntity.ok(CommonResponse.success(productService.saveProduct(productCreateRequestDto), "상품이 성공적으로 생성되었습니다."));
    }

    @GetMapping("{product_id}")
    public ResponseEntity<?> getProduct(@PathVariable(name = "product_id") UUID product_id) {
        return ResponseEntity.ok(CommonResponse.success(productService.getProduct(product_id), "상품이 성공적으로 조회되었습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return ResponseEntity.ok(CommonResponse.success(productService.getProducts(pageable), "상품목록이 성공적으로 조회되었습니다."));
    }

    @PutMapping("{product_id}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "product_id") UUID product_id, @RequestBody @Valid ProductUpdateRequestDto productUpdateRequestDto) {
        return ResponseEntity.ok(CommonResponse.success(productService.updateProduct(product_id, productUpdateRequestDto), "상품이 성공적으로 수정되었습니다."));

    }

    @DeleteMapping("{product_id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "product_id") UUID product_id) {
        return ResponseEntity.ok(CommonResponse.success(productService.deleteProduct(product_id), "상품이 성공적으로 삭제되었습니다."));
    }


}
