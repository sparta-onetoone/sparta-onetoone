package com.eureka.spartaonetoone.domain.ai.application;

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

import com.eureka.spartaonetoone.domain.ai.application.dto.request.AiRequestDto;
import com.eureka.spartaonetoone.domain.ai.application.dto.response.AiResponseDto;
import com.eureka.spartaonetoone.domain.ai.domain.Ai;
import com.eureka.spartaonetoone.domain.ai.domain.repository.AiRepository;
import com.eureka.spartaonetoone.domain.user.domain.User;

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

	public AiResponseDto recommendProductNames(@Valid AiRequestDto requestDto, User user) {
		AiResponseDto responseDto = getAIResponse(requestDto);

		Ai ai = Ai.fromRequestDtoAndResponseDtoToAI(requestDto, responseDto, user);

		aiRepository.save(ai);

		return responseDto;
	}

	private AiResponseDto getAIResponse(AiRequestDto requestDto) {
		URI uri = buildUri();

		Map<String, Object> requestBody = buildRequestBody(requestDto);

		RequestEntity<Map<String, Object>> requestEntity = RequestEntity.post(uri)
			.body(requestBody);

		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

		return fromJSONtoResponseDto(responseEntity.getBody());
	}

	private URI buildUri() {
		return UriComponentsBuilder.fromUriString(AI_REQUEST_URI)
			.path(AI_REQUEST_PATH)
			.queryParam("key", geminiApiKey)
			.encode()
			.build()
			.toUri();
	}

	private Map<String, Object> buildRequestBody(AiRequestDto requestDto) {
        /* Gemini API 요청 json 형식
        {
            "contents":[
            {
                "parts":[
                {
                    "text":"사용자가 입력한 prompt"
                }]
            }]
        }
        */
		// Gemini API에 보낼 Request Body
		Map<String, Object> requestBody = new HashMap<>();
		// 요청 형식의 "contents" : []
		Map<String, List<Map<String, Object>>> contents = new HashMap<>();
		// 요청 형식의 "parts" : []
		List<Map<String, Object>> parts = new ArrayList<>();
		// 요청 형식의 parts 내부 part Object, 여기서는 "text" : {}
		Map<String, Object> part = new HashMap<>();

		// 사용자가 입력한 prompt를 parts 내부 Object에 "text"로 추가
		part.put("text", requestDto.getPrompt() + MAX_LENGTH_PROMPT_MESSAGE);
		// "text"로 추가한 part를 parts 배열에 추가
		parts.add(part);
		// parts 배열을 contents 배열에 추가
		contents.put("parts", parts);
		//requestBody
		requestBody.put("contents", contents);

		return requestBody;
	}

	private AiResponseDto fromJSONtoResponseDto(String responseEntity) {
		JSONObject jsonResponse = new JSONObject(responseEntity);

		// 첫 번째 candidates에서 텍스트 추출
		JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);
		JSONObject content = candidate.getJSONObject("content");
		String answer = content.getJSONArray("parts").getJSONObject(0).getString("text");

		// AIResponseDto 객체 생성
		return new AiResponseDto(answer);
	}
}

