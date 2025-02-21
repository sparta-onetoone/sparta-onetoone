package com.eureka.spartaonetoone.ai.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiProductRecommendationResponseDto {
	private String answer;

	// from 메서드 추가
	public static AiProductRecommendationResponseDto from(String answer) {
		return new AiProductRecommendationResponseDto(answer);
	}

}
