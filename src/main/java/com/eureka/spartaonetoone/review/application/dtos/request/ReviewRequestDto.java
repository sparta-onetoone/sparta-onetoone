package com.eureka.spartaonetoone.review.application.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import com.eureka.spartaonetoone.review.domain.Review;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
	// 해당 리뷰가 연결된 주문의 식별자
	private UUID orderId;
	// 리뷰 평점
	private Integer rating;
	// 리뷰 내용
	private String content;
	// 리뷰 관련 이미지 URL
	private String image;

	public Review createReview() {
		return Review.createReview(orderId, rating, content, image);
	}
}
