package com.eureka.spartaonetoone.product.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.product.application.ProductService;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductCreateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductReduceRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductSearchRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductUpdateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.response.ProductGetResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController implements ProductApi {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody @Valid
                                         ProductCreateRequestDto productCreateRequestDto) {
        UUID savedProductId = productService.saveProduct(productCreateRequestDto);
        return ResponseEntity.ok(CommonResponse.success(savedProductId, "상품이 성공적으로 생성되었습니다."));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable(name = "productId") UUID product_id) {
        ProductGetResponseDto product = productService.getProduct(product_id);
        return ResponseEntity.ok(CommonResponse.success(product, "상품이 성공적으로 조회되었습니다."));
    }


    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "10") int limit) {
        Pageable pageable = getPageable(page, limit);
        Page<ProductGetResponseDto> products = productService.getProducts(pageable);
        return ResponseEntity.ok(CommonResponse.success(products, "상품목록이 성공적으로 조회되었습니다."));
    }

    private Pageable getPageable(int page, int limit) {
        return PageRequest.of(page - 1, limit);

    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable(name = "productId") UUID product_id, @RequestBody @Valid ProductUpdateRequestDto request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UUID updatedProductId = productService.updateProduct(product_id, request, userDetails);
        return ResponseEntity.ok(CommonResponse.success(updatedProductId, "상품이 성공적으로 수정되었습니다."));

    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable(name = "productId") UUID product_id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UUID deletedProductId = productService.deleteProduct(product_id, userDetails);
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

    @PatchMapping("/reduce-product")
    public ResponseEntity<?> reduceProduct(@RequestBody
                                           @Valid ProductReduceRequestDto request) {
        List<ProductReduceRequestDto.ReduceProductInfo> reduceProductInfoList = request.getReduceProductInfoList();

        List<UUID> productIds = reduceProductInfoList.stream().map(ProductReduceRequestDto.ReduceProductInfo::getProductId).toList();
        List<Integer> quantities = reduceProductInfoList.stream().map(ProductReduceRequestDto.ReduceProductInfo::getQuantity).toList();

        Boolean isReduced = productService.reduceProduct(productIds, quantities);

        if (isReduced) {
            return ResponseEntity.ok(CommonResponse.success(null, null));
        }

        return ResponseEntity.badRequest().body(CommonResponse.fail());

    }

}
