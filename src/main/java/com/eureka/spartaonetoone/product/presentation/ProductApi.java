package com.eureka.spartaonetoone.product.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductCreateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductReduceRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductSearchRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.request.ProductUpdateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.response.ProductGetResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "상품 API", description = "상품 관련 API")
public interface ProductApi {

    @Operation(summary = "상품 생성", description = "상품을 생성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 생성 성공",
                    content = @Content(schema = @Schema(implementation = UUID.class)))
    })
    ResponseEntity<?> saveProduct(ProductCreateRequestDto request);

    @Operation(summary = "상품 단건 조회", description = "상품 1개를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductGetResponseDto.class)))
    })
    ResponseEntity<?> getProduct(UUID productId);

    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<?> getProducts(int page, int limit);

    @Operation(summary = "상품 정보 검색", description = "상품 정보를 검색하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 정보 검색 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<?> searchProducts(ProductSearchRequestDto request, int page, int limit);


    @Operation(summary = "상품 재고 감소", description = "상품 재고를 감소하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 재고 감소 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    ResponseEntity<?> reduceProduct(ProductReduceRequestDto request);


    @Operation(summary = "상품 정보 수정", description = "상품 정보를 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UUID.class)))
    })
    ResponseEntity<?> updateProduct(UUID productId, ProductUpdateRequestDto request, UserDetailsImpl userDetails);

    @Operation(summary = "상품 정보 삭제", description = "상품 정보를 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 정보 삭제 성공",
                    content = @Content(schema = @Schema(implementation = UUID.class)))
    })
    ResponseEntity<?> deleteProduct(UUID productId, UserDetailsImpl userDetails);

}
