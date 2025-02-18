package com.eureka.spartaonetoone.common.client;

import com.eureka.spartaonetoone.common.client.dto.ReviewAggregateRequest;
import com.eureka.spartaonetoone.common.client.dto.ReviewAggregateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ReviewAggregateClient { // 어그리 빼자.

	private final WebClient webClient;

	// 예: http://review-service/api/v1/reviews/_aggregate
	@Value("${review.aggregate.url}")
	private String reviewAggregateUrl;

	public ReviewAggregateResponse getReviewAggregate(ReviewAggregateRequest request) {
		return webClient.post()
			.uri(reviewAggregateUrl)
			.bodyValue(request)
			.retrieve()
			.bodyToMono(ReviewAggregateResponse.class)
			.block(); // 동기 호출
	}
}
