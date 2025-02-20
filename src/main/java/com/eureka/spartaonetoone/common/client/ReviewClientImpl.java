package com.eureka.spartaonetoone.common.client;

import com.eureka.spartaonetoone.common.dtos.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewClientImpl implements ReviewClient {
	private final WebClient webClient;

	// 주문 ID 목록 기반 리뷰 조회 API URL
	private static final String REVIEWS_BY_ORDERS_URI = "/api/v1/reviews/";

	@Override
	public List<ReviewResponse> getReviews(List<UUID> orderIds) {
		return webClient.post()
			.uri(REVIEWS_BY_ORDERS_URI)  // 상수로 정의된 URI 사용
			.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			.bodyValue(orderIds)         // 주문 ID 목록을 JSON 배열로 전송
			.retrieve()
			.bodyToFlux(ReviewResponse.class)
			.collectList()
			.block();
	}
}