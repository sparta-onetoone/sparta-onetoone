package com.eureka.spartaonetoone.review.application.dto.response;

import com.eureka.spartaonetoone.review.domain.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(builderMethodName = "Builder", access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
	private UUID reviewId;
	private UUID orderId;    // 해당 리뷰가 연결된 주문의 식별자
	private Integer rating;
	private String content;
	private String image;
	private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private LocalDateTime deletedAt;

		// 정적 팩토리 메서드 - Review 엔티티의 데이터를 ReviewResponseDto로 변환
		public static ReviewResponseDto from(final Review review) {
			return Builder()
				.reviewId(review.getReviewId())
				.orderId(review.getOrderId())
				.rating(review.getRating())
				.content(review.getContent())
				.image(review.getImage())
				.createdAt(review.getCreatedAt())
			.updatedAt(review.getUpdatedAt())
			.deletedAt(review.getDeletedAt())
			.build();
	}
}