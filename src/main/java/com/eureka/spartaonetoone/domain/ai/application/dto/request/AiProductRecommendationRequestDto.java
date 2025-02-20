package com.eureka.spartaonetoone.domain.ai.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiProductRecommendationRequestDto {
	@NotBlank
	@Size(min = 2, max = 50)
	private String prompt;

}

