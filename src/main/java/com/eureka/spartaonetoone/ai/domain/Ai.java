package com.eureka.spartaonetoone.ai.domain;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.eureka.spartaonetoone.ai.application.dto.request.AiProductRecommendationRequestDto;
import com.eureka.spartaonetoone.ai.application.dto.response.AiProductRecommendationResponseDto;
import com.eureka.spartaonetoone.common.utils.TimeStamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_ai")
public class Ai extends TimeStamp {
	@Id
	@UuidGenerator
	@Column(name = "ai_id")
	private UUID id;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private String prompt;

	@Column(nullable = false)
	private String answer;

	@Builder
	private Ai(UUID userID, String prompt, String answer) {
		this.userId = userID;
		this.prompt = prompt;
		this.answer = answer;
	}

	// 정적 팩토리 메서드
	public static Ai create(AiProductRecommendationRequestDto requestDto,
		AiProductRecommendationResponseDto responseDto, UUID userId) {
		return Ai.builder()
			.prompt(requestDto.getPrompt())
			.answer(responseDto.getAnswer())
			.userID(userId)
			.build();
	}
}

