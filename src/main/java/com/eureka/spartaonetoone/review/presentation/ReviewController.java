package com.eureka.spartaonetoone.review.presentation;

import com.eureka.spartaonetoone.review.application.ReviewService;
import com.eureka.spartaonetoone.review.application.dtos.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;

import com.eureka.spartaonetoone.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	// 리뷰 생성
	@PostMapping
	public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
		ReviewResponseDto responseDto = reviewService.createReview(reviewRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 특정 리뷰 조회
	@GetMapping("/{review_id}")
	public ResponseEntity<ReviewResponseDto> getReview(@PathVariable("review_id") UUID reviewId) {
		ReviewResponseDto responseDto = reviewService.getReviewById(reviewId);
		return ResponseEntity.ok(responseDto);
	}

	// 특정주문(orderId)에 속한 리뷰 전체 목록 조회
	@GetMapping("/orders/{order_id}")
	public ResponseEntity<Page<ReviewResponseDto>> getReviewsByOrderId(@PathVariable("order_id") UUID orderId, Pageable pageable) {
		Page<ReviewResponseDto> responseDtos = reviewService.getReviewsByOrderId(orderId, pageable);
		return ResponseEntity.ok(responseDtos);
	}

	@GetMapping
	public ResponseEntity<Page<Review>> getAllReviews(Pageable pageable) {
		Page<Review> reviews = reviewService.getAllReviews(pageable);
		return ResponseEntity.ok(reviews);
	}

	// 리뷰 수정
	@PutMapping("/{review_id}")
	public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("review_id") UUID reviewId,
		@RequestBody ReviewRequestDto reviewRequestDto) {
		ReviewResponseDto responseDto = reviewService.updateReview(reviewId, reviewRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 리뷰 삭제
	@DeleteMapping("/{review_id}")
	public ResponseEntity<Void> deleteReview(@PathVariable("review_id") UUID reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseEntity.noContent().build();
	}

	// 리뷰 검색: 여러 orderIds를 받아 페이징 처리된 결과 반환
	@PostMapping("/search")
	public ResponseEntity<Page<ReviewResponseDto>> searchReviews(@RequestBody List<UUID> orderIds, Pageable pageable) {
		Page<ReviewResponseDto> reviews = reviewService.searchReviewsByOrderIds(orderIds, pageable);
		return ResponseEntity.ok(reviews);
	}
}
