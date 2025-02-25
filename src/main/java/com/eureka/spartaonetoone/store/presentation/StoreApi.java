package com.eureka.spartaonetoone.store.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
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

import java.util.UUID;

@Tag(name = "가게 API", description = "가게 관련 API")
public interface StoreApi {

    @Operation(summary = "가게 등록", description = "가게를 등록하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 등록 성공",
                    content = @Content(schema = @Schema(implementation = StoreResponseDto.class)))
    })
    ResponseEntity<CommonResponse<?>> createStore(StoreRequestDto requestDto);

    @Operation(summary = "가게 단건 조회", description = "특정 가게를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 조회 성공",
                    content = @Content(schema = @Schema(implementation = StoreResponseDto.class)))
    })
    ResponseEntity<CommonResponse<?>> getStore(UUID storeId);

    @Operation(summary = "가게 검색", description = "조건에 맞는 가게를 검색하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 검색 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<CommonResponse<?>> searchStores(String category, String name, Pageable pageable);

    @Operation(summary = "전체 가게 목록 조회", description = "전체 가게 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 가게 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = StoreResponseDto.class))))
    })
    ResponseEntity<CommonResponse<?>> getAllStores(Pageable pageable);

    @Operation(summary = "가게 수정", description = "특정 가게 정보를 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 수정 성공",
                    content = @Content(schema = @Schema(implementation = StoreResponseDto.class)))
    })
    ResponseEntity<CommonResponse<?>> updateStore(UUID storeId, StoreRequestDto requestDto, UserDetailsImpl userDetails);

    @Operation(summary = "가게 삭제", description = "특정 가게를 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가게 삭제 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    ResponseEntity<CommonResponse<?>> deleteStore(UUID storeId, UserDetailsImpl userDetails);
}
