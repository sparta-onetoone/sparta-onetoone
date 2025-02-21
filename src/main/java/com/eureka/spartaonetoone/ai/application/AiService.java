package com.eureka.spartaonetoone.ai.application;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.eureka.spartaonetoone.ai.application.dto.request.AiProductRecommendationRequestDto;
import com.eureka.spartaonetoone.ai.application.dto.response.AiProductRecommendationResponseDto;
import com.eureka.spartaonetoone.ai.domain.Ai;
import com.eureka.spartaonetoone.ai.domain.repository.AiRepository;
import com.eureka.spartaonetoone.user.domain.User;

import jakarta.validation.Valid;

@Service
public class AiService {
	private static final String AI_REQUEST_URI = "https://generativelanguage.googleapis.com";
	private static final String AI_REQUEST_PATH = "/v1beta/models/gemini-1.5-flash-latest:generateContent";
	private static final String MAX_LENGTH_PROMPT_MESSAGE = ", 답변을 최대한 간결하게 50자 이하로";

	@Value("${gemini.api.key}")
	private String geminiApiKey;
	private final RestTemplate restTemplate;
	private final AiRepository aiRepository;

	@Autowired
	public AiService(RestTemplateBuilder builder, AiRepository aiRepository) {
		this.restTemplate = builder.build();
		this.aiRepository = aiRepository;
	}

	/**
	 * 제품 이름 추천을 위한 메서드
	 * 사용자가 입력한 데이터에 대해 AI가 생성한 추천을 받아오고, 이를 DB에 저장
	 */
	public AiProductRecommendationResponseDto recommendProductNames(@Valid AiProductRecommendationRequestDto requestDto,
		User user) {
		AiProductRecommendationResponseDto responseDto = getAIResponse(requestDto);

		// 도메인 생성 메서드는 create 사용
		Ai ai = Ai.create(requestDto, responseDto, user);

		aiRepository.save(ai);

		return responseDto;
	}

	/**
	 * AI에게 요청을 보내고 응답을 받아오는 메서드
	 */
	private AiProductRecommendationResponseDto getAIResponse(AiProductRecommendationRequestDto requestDto) {
		URI uri = buildUri();

		Map<String, Object> requestBody = buildRequestBody(requestDto);

		RequestEntity<Map<String, Object>> requestEntity = RequestEntity.post(uri)
			.body(requestBody);

		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

		// 리스폰스 DTO 추출 시 from 사용
		return AiProductRecommendationResponseDto.from(responseEntity.getBody());
	}

	/**
	 * AI 요청을 위한 URI를 생성하는 메서드
	 */
	private URI buildUri() {
		return UriComponentsBuilder.fromUriString(AI_REQUEST_URI)
			.path(AI_REQUEST_PATH)
			.queryParam("key", geminiApiKey)
			.encode()
			.build()
			.toUri();
	}

	/**
	 * AI에 보낼 요청 본문을 생성하는 메서드
	 * Gemini API의 요청 형식에 맞게 데이터를 구성
	 */
	private Map<String, Object> buildRequestBody(AiProductRecommendationRequestDto requestDto) {
		Map<String, Object> requestBody = new HashMap<>();
		Map<String, List<Map<String, Object>>> contents = new HashMap<>();
		List<Map<String, Object>> parts = new ArrayList<>();
		Map<String, Object> part = new HashMap<>();

		part.put("text", requestDto.getPrompt() + MAX_LENGTH_PROMPT_MESSAGE);
		parts.add(part);
		contents.put("parts", parts);
		requestBody.put("contents", contents);

		return requestBody;
	}

	/**
	 * API 응답을 JSON 형식에서 AiResponseDto 객체로 변환하는 메서드
	 */
	private AiProductRecommendationResponseDto fromJSONtoResponseDto(String responseEntity) {
		JSONObject jsonResponse = new JSONObject(responseEntity);
		JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);
		JSONObject content = candidate.getJSONObject("content");
		String answer = content.getJSONArray("parts").getJSONObject(0).getString("text");

		// 리스폰스 DTO 생성 시 from 사용
		return AiProductRecommendationResponseDto.from(answer);
	}
}