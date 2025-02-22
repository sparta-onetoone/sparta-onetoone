package com.eureka.spartaonetoone.ai.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eureka.spartaonetoone.ai.domain.Ai;
import com.eureka.spartaonetoone.ai.domain.repository.AiRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.eureka.spartaonetoone.ai.application.AiService;
import com.eureka.spartaonetoone.ai.application.dto.request.AiProductRecommendationRequestDto;
import com.eureka.spartaonetoone.ai.application.dto.response.AiProductRecommendationResponseDto;
import com.eureka.spartaonetoone.user.domain.User;

class AiServiceTest {

	@Mock
	private AiRepository aiRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private AiService aiService;

	private AiProductRecommendationRequestDto requestDto;
	private AiProductRecommendationResponseDto responseDto;
	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// Mock User object (you can customize it further as needed)
		user = mock(User.class);

		// Sample Request DTO
		requestDto = new AiProductRecommendationRequestDto("이 제품은 어떤가요?");

		// Sample Response DTO from AI (mocked response)
		responseDto = new AiProductRecommendationResponseDto("추천된 제품명");

		// RestTemplate mock
		RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
		when(restTemplateBuilder.build()).thenReturn(restTemplate);
	}

	@Test
	void recommendProductNames_ShouldReturnResponseDto() {
		// Arrange
		String jsonResponse = "{ \"candidates\": [{ \"content\": { \"parts\": [{ \"text\": \"추천된 제품명\" }] } }] }";
		ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

		// RestTemplate의 exchange 메서드에 대한 명확한 호출
		when(restTemplate.exchange(
			any(RequestEntity.class), eq(String.class) // 정확한 타입을 지정
		)).thenReturn(responseEntity);

		// Act
		AiProductRecommendationResponseDto result = aiService.recommendProductNames(requestDto, user);

		// Assert
		assertNotNull(result);
		assertEquals("추천된 제품명", result.getAnswer());

		// Verify that AiRepository.save was called once with any Ai entity
		verify(aiRepository, times(1)).save(any(Ai.class));
	}

	@Test
	void recommendProductNames_ShouldHandleErrorResponse() {
		// Arrange
		String errorResponse = "{ \"error\": \"Invalid request\" }";
		ResponseEntity<String> responseEntity = new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

		// RestTemplate의 exchange 메서드에 대한 명확한 호출
		when(restTemplate.exchange(
			any(RequestEntity.class), eq(String.class) // 정확한 타입을 지정
		)).thenReturn(responseEntity);

		// Act
		AiProductRecommendationResponseDto result = aiService.recommendProductNames(requestDto, user);

		// Assert
		assertNotNull(result);
		assertEquals("", result.getAnswer());  // Assuming AI returns an empty string in case of error (you can change this behavior)
	}
}
