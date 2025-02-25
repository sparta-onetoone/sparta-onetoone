package com.eureka.spartaonetoone.category.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.category.application.dtos.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "카테고리 API", description = "카테고리 관련 API")
public interface CategoryApi {

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 생성 성공",
                    content = @Content(schema = @Schema(implementation = CategoryDto.class)))
    })
    ResponseEntity<CommonResponse<?>> createCategory(String name);

    @Operation(summary = "카테고리 수정", description = "카테고리 이름을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공",
                    content = @Content(schema = @Schema(implementation = CategoryDto.class)))
    })
    ResponseEntity<CommonResponse<?>> updateCategory(UUID categoryId, String name);

    @Operation(summary = "카테고리 필터 조회", description = "이름을 기준으로 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    ResponseEntity<CommonResponse<?>> getCategoriesByFilter(String name);

    @Operation(summary = "카테고리 단건 조회", description = "카테고리 ID를 기반으로 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 단건 조회 성공",
                    content = @Content(schema = @Schema(implementation = CategoryDto.class)))
    })
    ResponseEntity<CommonResponse<?>> getCategoryById(UUID categoryId);

    @Operation(summary = "전체 카테고리 조회", description = "전체 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 카테고리 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    ResponseEntity<CommonResponse<?>> getAllCategories();

    @Operation(summary = "카테고리 검색", description = "여러 카테고리 UUID 목록을 기반으로 페이징된 카테고리 DTO를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 검색 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<CommonResponse<?>> searchCategories(List<UUID> categoryIds, Pageable pageable);
}
