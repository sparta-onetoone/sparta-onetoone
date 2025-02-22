package com.eureka.spartaonetoone.ai.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import com.eureka.spartaonetoone.ai.application.dto.request.AiProductRecommendationRequestDto;
import com.eureka.spartaonetoone.ai.application.dto.response.AiProductRecommendationResponseDto;
import com.eureka.spartaonetoone.ai.domain.Ai;
import com.eureka.spartaonetoone.ai.domain.repository.AiRepository;
import com.eureka.spartaonetoone.user.domain.User;

@SpringBootTest
class AiServiceTest {

	@Autowired
	private AiService aiService;

	@MockitoBean
	private AiRepository aiRepository;

	@MockitoBean
	private RestTemplate restTemplate;

	@DisplayName("AI가 제품 추천을 수행하고 데이터를 저장한다.")
	@Test
	void recommend_product_names_test() {
		// Given
		User user = mock(User.class);
		AiProductRecommendationRequestDto requestDto = new AiProductRecommendationRequestDto("추천할 제품명");

		// 응답 모의 값 설정 (서비스에서 반환하는 응답을 정확하게 설정)
		String mockResponse = "{\n" +
			"  \"candidates\": [\n" +
			"    {\n" +
			"      \"content\": {\n" +
			"        \"parts\": [\n" +
			"          {\n" +
			"            \"text\": \"추천된 제품명\"\n" +
			"          }\n" +
			"        ]\n" +
			"      }\n" +
			"    }\n" +
			"  ]\n" +
			"}";

		// restTemplate.exchange()가 mockResponse를 반환하도록 설정
		when(restTemplate.exchange(any(), eq(String.class)))
			.thenReturn(ResponseEntity.ok(mockResponse));

		// When
		AiProductRecommendationResponseDto response = aiService.recommendProductNames(requestDto, user);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.getAnswer()).isEqualTo("추천된 제품명"); // 예상한 답변
		verify(aiRepository, times(1)).save(any(Ai.class)); // DB 저장 검증
	}
}