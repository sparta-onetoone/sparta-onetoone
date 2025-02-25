package com.eureka.spartaonetoone.review.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.review.application.ReviewService;
import com.eureka.spartaonetoone.review.application.dtos.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;
import com.eureka.spartaonetoone.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController implements ReviewApi {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	// 리뷰 생성
	@Override
	@PostMapping
	@Secured({"ROLE_CUSTOMER"})
	public ResponseEntity<CommonResponse<?>> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
		ReviewResponseDto responseDto = reviewService.createReview(reviewRequestDto);
		return ResponseEntity.ok(CommonResponse.success(responseDto, "리뷰 생성 성공"));
	}

	// 특정 리뷰 조회
	@Override
	@GetMapping("/{review_id}")
	public ResponseEntity<CommonResponse<?>> getReview(@PathVariable("review_id") UUID reviewId) {
		ReviewResponseDto responseDto = reviewService.getReviewById(reviewId);
		return ResponseEntity.ok(CommonResponse.success(responseDto, "리뷰 조회 성공"));
	}

	// 특정 주문(orderId)에 속한 리뷰 전체 목록 조회 (페이징)
	@Override
	@GetMapping("/orders/{order_id}")
	public ResponseEntity<CommonResponse<?>> getReviewsByOrderId(@PathVariable("order_id") UUID orderId, Pageable pageable) {
		Page<ReviewResponseDto> responseDtos = reviewService.getReviewsByOrderId(orderId, pageable);
		return ResponseEntity.ok(CommonResponse.success(responseDtos, "주문별 리뷰 조회 성공"));
	}

	// 전체 리뷰 조회 (페이징)
	@Override
	@GetMapping
	public ResponseEntity<CommonResponse<?>> getAllReviews(Pageable pageable) {
		Page<Review> reviews = reviewService.getAllReviews(pageable);
		return ResponseEntity.ok(CommonResponse.success(reviews, "전체 리뷰 조회 성공"));
	}

	// 리뷰 수정
	@Override
	@PutMapping("/{review_id}")
	@Secured({"ROLE_CUSTOMER"})
	public ResponseEntity<CommonResponse<?>> updateReview(@PathVariable("review_id") UUID reviewId,
														  @RequestBody ReviewRequestDto reviewRequestDto) {
		ReviewResponseDto responseDto = reviewService.updateReview(reviewId, reviewRequestDto);
		return ResponseEntity.ok(CommonResponse.success(responseDto, "리뷰 수정 성공"));
	}

	// 리뷰 삭제
	@Override
	@DeleteMapping("/{review_id}")
	@Secured({"ROLE_CUSTOMER"})
	public ResponseEntity<CommonResponse<?>> deleteReview(@PathVariable("review_id") UUID reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseEntity.ok(CommonResponse.success(null, "리뷰 삭제 성공"));
	}

	// 리뷰 검색: 여러 orderIds를 받아 페이징 처리된 결과 반환
	@Override
	@PostMapping("/search")
	public ResponseEntity<CommonResponse<?>> searchReviews(@RequestBody List<UUID> orderIds, Pageable pageable) {
		Page<ReviewResponseDto> reviews = reviewService.searchReviewsByOrderIds(orderIds, pageable);
		return ResponseEntity.ok(CommonResponse.success(reviews, "리뷰 검색 성공"));
	}
}
