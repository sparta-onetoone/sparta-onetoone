package com.eureka.spartaonetoone.review.domain.repository;

import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CustomReviewRepository {
    Page<ReviewResponseDto> searchReviews(List<UUID> orderIds, Pageable pageable);
}
