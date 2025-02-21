package com.eureka.spartaonetoone.ai.domain;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.eureka.spartaonetoone.common.utils.TimeStamp;
import com.eureka.spartaonetoone.ai.application.dto.request.AiProductRecommendationRequestDto;
import com.eureka.spartaonetoone.ai.application.dto.response.AiProductRecommendationResponseDto;
import com.eureka.spartaonetoone.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_username", nullable = false)
	private User user;

	@Column(nullable = false)
	private String prompt;

	@Column(nullable = false)
	private String answer;

	@Builder
	private Ai(User user, String prompt, String answer) {
		this.user = user;
		this.prompt = prompt;
		this.answer = answer;
	}

	// 정적 팩토리 메서드
	public static Ai fromRequestDtoAndResponseDtoToAI(AiProductRecommendationRequestDto requestDto,
		AiProductRecommendationResponseDto responseDto, User user) {
		return Ai.builder()
			.prompt(requestDto.getPrompt())
			.answer(responseDto.getAnswer())
			.user(user)
			.build();
	}
}

