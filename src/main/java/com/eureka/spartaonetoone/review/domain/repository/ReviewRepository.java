package com.eureka.spartaonetoone.review.domain.repository;

import com.eureka.spartaonetoone.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
	// 특정 주문(orderId)에 속한 모든 리뷰를 조회하는 메서드
	List<Review> findByOrderId(UUID orderId);
}