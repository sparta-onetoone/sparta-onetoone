package com.eureka.spartaonetoone.common.client;

import com.eureka.spartaonetoone.common.dtos.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewClient {

	List<ReviewResponse> getReviews(List<UUID> orderIds);
}