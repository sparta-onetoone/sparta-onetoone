package com.eureka.spartaonetoone.ai.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.ai.application.AiService;
import com.eureka.spartaonetoone.ai.application.dto.request.AiProductRecommendationRequestDto;
import com.eureka.spartaonetoone.ai.application.dto.response.AiProductRecommendationResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ai")
public class AiController {
	private final AiService aiService;

	@Secured("ROLE_ADMIN")
	@PostMapping()
	public ResponseEntity<?> recommendProductNames(@Valid @RequestBody AiProductRecommendationRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		AiProductRecommendationResponseDto aiResponseDto = aiService.recommendProductNames(requestDto,
			userDetails.getUser());

		return ResponseEntity.status(HttpStatus.CREATED).body(aiResponseDto);
	}
}

