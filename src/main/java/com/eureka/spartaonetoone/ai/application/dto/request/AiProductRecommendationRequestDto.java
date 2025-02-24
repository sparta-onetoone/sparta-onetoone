package com.eureka.spartaonetoone.ai.application.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiProductRecommendationRequestDto {
	@NotBlank
	@Size(min = 2, max = 50)
	@JsonProperty("prompt")
	private String prompt;

	@JsonCreator
	public AiProductRecommendationRequestDto(@JsonProperty("prompt") String prompt) {
		this.prompt = prompt;
	}

}

