package com.eureka.spartaonetoone.ai;

import com.eureka.spartaonetoone.ai.application.AiService;
import com.eureka.spartaonetoone.ai.application.dto.request.AiProductRecommendationRequestDto;
import com.eureka.spartaonetoone.ai.application.dto.response.AiProductRecommendationResponseDto;
import com.eureka.spartaonetoone.ai.domain.repository.AiRepository;
import com.eureka.spartaonetoone.user.domain.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
@SpringBootTest
public class AiServiceTest {

	@Mock
	private RestTemplateBuilder restTemplateBuilder;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private AiRepository aiRepository;

	@InjectMocks
	private AiService aiService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(restTemplateBuilder.build()).thenReturn(restTemplate);
	}

	@Test
	public void testRecommendProductNames() throws Exception {
		// Mock AI 응답 (Gemini API 응답 형식)
		String mockResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"AI 추천 제품\"}]}}]}";

		// RestTemplate의 exchange() 메서드가 호출되면 mockResponse를 반환하도록 설정
		when(restTemplate.exchange(
			any(URI.class),  // URI는 어떤 값이든 상관 없음
			eq(HttpMethod.POST),  // HTTP POST 메서드를 사용
			any(HttpEntity.class),  // HttpEntity는 어떤 값이든 상관 없음
			eq(String.class)  // 응답은 String 타입으로 반환
		)).thenReturn(ResponseEntity.ok(mockResponse));  // ResponseEntity의 body로 mockResponse를 반환

		// 테스트할 데이터 준비
		AiProductRecommendationRequestDto requestDto = new AiProductRecommendationRequestDto("test prompt");

		// User 객체 준비 (이 예시에서는 간단하게 mock을 사용)
		User user = mock(User.class);

		// 실제 서비스 메서드 호출
		AiProductRecommendationResponseDto responseDto = aiService.recommendProductNames(requestDto, user);

		// 결과 검증
		assertNotNull(responseDto);  // 응답이 null이 아님을 확인
		assertEquals("AI 추천 제품", responseDto.getAnswer());  // 응답의 answer가 "AI 추천 제품"인지 확인
	}
}