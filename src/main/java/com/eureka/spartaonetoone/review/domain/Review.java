package com.eureka.spartaonetoone.review.domain;

import jakarta.persistence.*;
import com.eureka.spartaonetoone.common.utils.TimeStamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_review")
public class Review extends TimeStamp {

	@Id
	@GeneratedValue
	@Column(name = "review_id")
	private UUID reviewId;

	// 주문과 연결된 리뷰의 경우 주문 ID를 저장
	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	private String content;

	@Column(name = "image", length = 255)
	private String image;

	// Private 생성자 : 오직 정적 팩토리 메서드와 빌더를 통해서만 객체 생성
	@Builder(builderMethodName = "builder", access = AccessLevel.PRIVATE)
	private Review(UUID orderId, Integer rating, String content, String image) {
		this.orderId = orderId;
		this.rating = rating;
		this.content = content;
		this.image = image;
	}

	// 정적 팩토리 메서드 - 필요한 필드들을 전달받아 Review 엔티티를 생성
	public static Review createReview(UUID orderId, Integer rating, String content, String image) {
		return builder()
			.orderId(orderId)
			.rating(rating != null ? rating : 0)
			.content(content)
			.image(image)
			.build();
	}

	// 엔티티 내부 업데이트 메서드 - Service에서 전달받은 값을 이용해 기존 Review 엔티티의 필드를 업데이트
	public void update(String content, Integer rating, String image) {
		this.content = content;
		this.rating = rating != null ? rating : this.rating;
		if (image != null) {
			this.image = image;
		}
	}

	// delete 처리 메서드 - deletedAt 필드에 현재 시간을 설정하여 실제 삭제 없이 삭제된 것으로 표시
	public void markDeleted() {
		this.deletedAt = LocalDateTime.now();
	}
}
