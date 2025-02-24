package com.eureka.spartaonetoone.review.presentation;

import com.eureka.spartaonetoone.review.application.ReviewService;
import com.eureka.spartaonetoone.review.application.dtos.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	@GetMapping
	public ResponseEntity<Page<ReviewResponseDto>> getReviewsByOrderId(@RequestParam("order_id") UUID orderId, Pageable pageable) {
		Page<ReviewResponseDto> responseDtos = reviewService.getReviewsByOrderId(orderId, pageable);
		return ResponseEntity.ok(responseDtos);
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
}
