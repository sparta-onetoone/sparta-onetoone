package com.eureka.spartaonetoone.review.application;

import com.eureka.spartaonetoone.review.application.dto.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dto.response.ReviewResponseDto;
import com.eureka.spartaonetoone.review.domain.Review;
import com.eureka.spartaonetoone.review.domain.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;
	@InjectMocks
	private ReviewService reviewService;

	// 리뷰 등록 성공
	@Test
	public void testCreateReviewSuccess() {
		// given
		UUID orderId = UUID.randomUUID();
		ReviewRequestDto requestDto = new ReviewRequestDto(
			orderId, 5, "훌륭한 리뷰 내용", "http://example.com/image.png"
		);
		// Review.of()를 통해 생성된 Review 엔티티
		Review review = Review.of(orderId, 5, "훌륭한 리뷰 내용", "http://example.com/image.png");
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		// when : createReview 메서드 호출
		ReviewResponseDto response = reviewService.createReview(requestDto);

		// then : 반환된 DTO 검증
		assertThat(response).isNotNull();
		assertThat(response.getRating()).isEqualTo(5);
		assertThat(response.getContent()).isEqualTo("훌륭한 리뷰 내용");
		verify(reviewRepository, times(1)).save(any(Review.class));
	}

	//특정 리뷰 조회 성공
	@Test
	public void testGetReviewByIdSuccess() {
		// given
		UUID reviewId = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		Review review = Review.of(orderId, 4, "좋은 리뷰", "http://example.com/img.jpg");
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		// when: getReviewById 호출
		ReviewResponseDto response = reviewService.getReviewById(reviewId);

		// then: 반환된 DTO의 필드 검증
		assertThat(response).isNotNull();
		assertThat(response.getRating()).isEqualTo(4);
		assertThat(response.getContent()).isEqualTo("좋은 리뷰");
		verify(reviewRepository, times(1)).findById(reviewId);
	}

	// 테스트: 리뷰 수정 성공
	@Test
	public void testUpdateReviewSuccess() {
		// given: 기존 리뷰 엔티티 생성 및 모의
		UUID reviewId = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		Review existingReview = Review.of(orderId, 3, "평균 리뷰", "oldImg.png");
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
		when(reviewRepository.save(existingReview)).thenReturn(existingReview);

		// 수정 DTO 생성 (리뷰 내용과 평점, 이미지 변경)
		ReviewRequestDto updateDto = new ReviewRequestDto(
			orderId, 4, "업데이트된 리뷰 내용", "newImg.png"
		);

		// when: updateReview 호출
		ReviewResponseDto updatedResponse = reviewService.updateReview(reviewId, updateDto);

		// then: 반환된 DTO 검증
		assertThat(updatedResponse).isNotNull();
		assertThat(updatedResponse.getContent()).isEqualTo("업데이트된 리뷰 내용");
		assertThat(updatedResponse.getRating()).isEqualTo(4);
		assertThat(updatedResponse.getImage()).isEqualTo("newImg.png");
		verify(reviewRepository, times(1)).save(existingReview);
	}

	// 리뷰 삭제 성공
	@Test
	public void testDeleteReviewSuccess() {
		// given: 기존 리뷰 엔티티 생성 및 모의
		UUID reviewId = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		Review existingReview = Review.of(orderId, 5, "삭제될 리뷰", "deleteImg.png");
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
		when(reviewRepository.save(existingReview)).thenReturn(existingReview);

		// when : deleteReview 호출
		reviewService.deleteReview(reviewId);

		// then : soft delete 후 deletedAt 필드가 설정되어야 함
		assertThat(existingReview.getDeletedAt()).isNotNull();
		verify(reviewRepository, times(1)).save(existingReview);
	}

	// 테스트: 특정 주문에 속한 리뷰 목록 조회 성공 (페이지네이션 지원)
	@Test
	public void testGetReviewsByOrderId() {
		// given: 특정 주문 ID로 Review 목록 조회 모의
		UUID orderId = UUID.randomUUID();
		Review review1 = Review.of(orderId, 4, "리뷰 1", "img1.png");
		Review review2 = Review.of(orderId, 5, "리뷰 2", "img2.png");
		when(reviewRepository.findByOrderId(orderId)).thenReturn(Arrays.asList(review1, review2));
		Pageable pageable = Pageable.unpaged();

		// when: getReviewsByOrderId 호출
		Page<ReviewResponseDto> page = reviewService.getReviewsByOrderId(orderId, pageable);

		// then: 반환된 Page 객체 검증
		assertThat(page).isNotNull();
		assertThat(page.getTotalElements()).isEqualTo(2);
		verify(reviewRepository, times(1)).findByOrderId(orderId);
	}
}
