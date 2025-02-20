package com.eureka.spartaonetoone.domain.ai.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.domain.ai.application.AiService;
import com.eureka.spartaonetoone.domain.ai.application.dto.request.AiRequestDto;
import com.eureka.spartaonetoone.domain.ai.application.dto.response.AiResponseDto;
import com.eureka.spartaonetoone.domain.user.infrastructure.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AiController {
	private final AiService aiService;

	@Secured("ROLE_ADMIN")
	@PostMapping("/intro")
	public ResponseEntity<?> recommendProductNames(@Valid @RequestBody AiRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		AiResponseDto aiResponseDto = aiService.recommendProductNames(requestDto, userDetails.getUser());

		return ResponseEntity.status(HttpStatus.CREATED).body(aiResponseDto);
	}
}

