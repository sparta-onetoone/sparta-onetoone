package com.eureka.spartaonetoone.review.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.review.application.dtos.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;
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

@Tag(name = "리뷰 API", description = "리뷰 관련 API")
public interface ReviewApi {

    @Operation(summary = "리뷰 생성", description = "새로운 리뷰를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 생성 성공",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDto.class)))
    })
    ResponseEntity<CommonResponse<?>> createReview(ReviewRequestDto reviewRequestDto);

    @Operation(summary = "리뷰 조회", description = "리뷰 ID를 기반으로 리뷰를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDto.class)))
    })
    ResponseEntity<CommonResponse<?>> getReview(UUID reviewId);

    @Operation(summary = "주문별 리뷰 조회", description = "특정 주문(orderId)에 속한 리뷰들을 페이징 처리하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문별 리뷰 조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<CommonResponse<?>> getReviewsByOrderId(UUID orderId, Pageable pageable);

    @Operation(summary = "전체 리뷰 조회", description = "전체 리뷰 목록을 페이징 처리하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 리뷰 조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<CommonResponse<?>> getAllReviews(Pageable pageable);

    @Operation(summary = "리뷰 수정", description = "리뷰 ID를 기반으로 리뷰 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDto.class)))
    })
    ResponseEntity<CommonResponse<?>> updateReview(UUID reviewId, ReviewRequestDto reviewRequestDto);

    @Operation(summary = "리뷰 삭제", description = "리뷰 ID를 기반으로 리뷰를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    ResponseEntity<CommonResponse<?>> deleteReview(UUID reviewId);

    @Operation(summary = "리뷰 검색", description = "여러 주문 ID(orderIds)를 기반으로 리뷰를 검색하여 페이징 처리된 결과를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 검색 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<CommonResponse<?>> searchReviews(List<UUID> orderIds, Pageable pageable);
}
