package com.eureka.spartaonetoone.domain.ai.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class AiRequestDto {
	@NotBlank
	@Size(min = 2, max = 50)
	private String prompt;


}

