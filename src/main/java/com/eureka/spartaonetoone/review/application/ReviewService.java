package com.eureka.spartaonetoone.review.application;

import com.eureka.spartaonetoone.review.application.dtos.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;
import com.eureka.spartaonetoone.review.application.exception.ReviewException;
import com.eureka.spartaonetoone.review.domain.Review;
import com.eureka.spartaonetoone.review.domain.repository.ReviewRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;

	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	// 리뷰 등록 - DTO를 엔티티로 변환한 후 저장하고, 저장된 엔티티를 DTO로 변환하여 반환
	public ReviewResponseDto createReview(@Valid ReviewRequestDto dto) {
		Review review = dto.createReview();
		Review savedReview = reviewRepository.save(review);
		return ReviewResponseDto.from(savedReview);
	}

	// 특정 리뷰 조회 - 주어진 reviewId로 Review 엔티티를 조회하며, 존재하지 않으면 CustomException을 발생
	public ReviewResponseDto getReviewById(UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewException.ReviewNotFoundException::new);
		return ReviewResponseDto.from(review);
	}

	// 특정 주문(orderId)에 속한 리뷰 목록 조회
	// ReviewRepository의 findByOrderId()를 사용하여 해당 주문의 모든 리뷰를 조회
	// 리스트를 Pageable로 감싸서 반환
	public Page<ReviewResponseDto> getReviewsByOrderId(UUID orderId, Pageable pageable) {
		List<Review> reviews = reviewRepository.findByOrderId(orderId);
		List<ReviewResponseDto> dtoList = reviews.stream()
			.map(ReviewResponseDto::from)
			.collect(Collectors.toList());
		return new PageImpl<>(dtoList, pageable, dtoList.size());
	}

	// 리뷰 수정 - 특정 reviewId에 해당하는 Review 엔티티를 조회한 후, DTO의 값으로 업데이트하고 저장
	public ReviewResponseDto updateReview(UUID reviewId, @Valid ReviewRequestDto dto) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewException.ReviewNotFoundException::new);
		review.update(dto.getContent(), dto.getRating(), dto.getImage());
		Review updatedReview = reviewRepository.save(review);
		return ReviewResponseDto.from(updatedReview);
	}


	// 리뷰 삭제 - 특정 reviewId로 Review 엔티티를 조회한 후, markDeleted()를 호출
	public void deleteReview(UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(ReviewException.ReviewNotFoundException::new);
		review.markDeleted();
		reviewRepository.save(review);
	}
}