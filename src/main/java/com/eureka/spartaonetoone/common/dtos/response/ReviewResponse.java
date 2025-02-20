package com.eureka.spartaonetoone.common.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
	// 해당 가게의 총 리뷰 수
	private int reviewCount;
	// 해당 가게의 평균 평점
	private float totalRating;

}
