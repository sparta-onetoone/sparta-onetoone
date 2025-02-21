package com.eureka.spartaonetoone.store.presentation.controller;

import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StoreControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	// 인증 정보를 포함한 TestRestTemplate 생성 (예: user와 password로 기본 인증)
	private TestRestTemplate getAuthRestTemplate() {
		return restTemplate.withBasicAuth("user", "password");
	}

	// given: 유효한 StoreRequestDto 생성, when: 인증 정보를 포함한 POST 요청, then: 200 OK와 응답 내용 검증
	@Test
	public void testCreateAndGetStoreSuccess() throws Exception {
		// given: 테스트에 사용할 StoreRequestDto 생성
		StoreRequestDto requestDto = new StoreRequestDto(
			UUID.randomUUID(),                           // userId
			"통합 테스트 가게",                           // 가게 이름
			"OPEN",                                      // 가게 상태
			"010-1111-2222",                             // 전화번호
			"통합 테스트 설명",                          // 설명
			1200,                                        // 최소 주문 금액
			300,                                         // 배달비
			0.0f,                                        // 평점 (초기값)
			0,                                           // 리뷰 수 (초기값)
			"생성자",                                    // 생성자
			UUID.randomUUID()                            // categoryId
		);

		// when: 인증 정보를 포함한 TestRestTemplate 사용
		TestRestTemplate authRestTemplate = getAuthRestTemplate();
		ResponseEntity<StoreResponseDto> postResponse = authRestTemplate.postForEntity("/api/v1/stores", requestDto, StoreResponseDto.class);
		assertThat(postResponse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
		StoreResponseDto createdStore = postResponse.getBody();
		assertThat(createdStore).isNotNull();
		UUID storeId = createdStore.getStoreId();

		// when: 인증 정보를 포함한 GET 요청으로 생성된 가게 조회
		ResponseEntity<StoreResponseDto> getResponse = authRestTemplate.getForEntity("/api/v1/stores/" + storeId, StoreResponseDto.class);
		// then: 반환된 가게의 이름이 요청한 값과 일치하는지 검증
		assertThat(getResponse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
		StoreResponseDto fetchedStore = getResponse.getBody();
		assertThat(fetchedStore).isNotNull();
		assertThat(fetchedStore.getName()).isEqualTo("통합 테스트 가게");
	}

	// given: 존재하지 않는 storeId 생성, when: GET 요청, then: 404 NOT_FOUND 검증
	@Test
	public void testGetStoreNotFound() {
		UUID invalidId = UUID.randomUUID();
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/stores/" + invalidId, String.class);
		assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
	}

	// given: 전체 가게 목록 조회를 위한 GET 요청, when: 요청 수행, then: 200 OK 상태 코드 검증
	@Test
	public void testGetAllStores() {
		ResponseEntity<Page> response = restTemplate.getForEntity("/api/v1/stores", Page.class);
		assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
	}

	// given: 가게 생성 후 수정할 DTO 생성, when: PUT 요청, then: 수정된 데이터 검증
	@Test
	public void testUpdateStoreSuccess() throws Exception {
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		// 가게 생성
		StoreRequestDto createDto = new StoreRequestDto(
			userId,
			"기존 가게",
			"OPEN",
			"010-1234-5678",
			"기존 설명",
			1000,
			500,
			0.0f,
			0,
			"생성자",
			categoryId
		);
		TestRestTemplate authRestTemplate = getAuthRestTemplate();
		ResponseEntity<StoreResponseDto> postResponse = authRestTemplate.postForEntity("/api/v1/stores", createDto, StoreResponseDto.class);
		UUID storeId = postResponse.getBody().getStoreId();

		// 수정할 데이터 생성
		StoreRequestDto updateDto = new StoreRequestDto(
			userId,
			"수정된 가게",
			"CLOSED",
			"010-9999-8888",
			"수정된 설명",
			1500,
			700,
			0.0f,
			0,
			"수정자",
			categoryId
		);
		HttpEntity<StoreRequestDto> requestEntity = new HttpEntity<>(updateDto);
		ResponseEntity<Void> putResponse = authRestTemplate.exchange("/api/v1/stores/" + storeId, HttpMethod.PUT, requestEntity, Void.class);
		assertThat(putResponse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);

		// 수정 후 GET 요청으로 변경된 값 검증
		ResponseEntity<StoreResponseDto> getResponse = authRestTemplate.getForEntity("/api/v1/stores/" + storeId, StoreResponseDto.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
		StoreResponseDto updatedStore = getResponse.getBody();
		assertThat(updatedStore).isNotNull();
		assertThat(updatedStore.getName()).isEqualTo("수정된 가게");
		assertThat(updatedStore.getState()).isEqualTo("CLOSED");
	}

	// given: 가게 생성 후, DELETE 요청 수행, when: DELETE 후 GET 요청, then: 404 NOT_FOUND 검증
	@Test
	public void testDeleteStore() {
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		StoreRequestDto createDto = new StoreRequestDto(
			userId,
			"삭제할 가게",
			"OPEN",
			"010-1234-5678",
			"삭제할 설명",
			1000,
			500,
			0.0f,
			0,
			"생성자",
			categoryId
		);
		TestRestTemplate authRestTemplate = getAuthRestTemplate();
		ResponseEntity<StoreResponseDto> postResponse = authRestTemplate.postForEntity("/api/v1/stores", createDto, StoreResponseDto.class);
		UUID storeId = postResponse.getBody().getStoreId();

		// when: DELETE 요청으로 가게 삭제 (soft delete)
		authRestTemplate.delete("/api/v1/stores/" + storeId);

		// then: 삭제 후, GET 요청 시 404 NOT_FOUND 검증
		ResponseEntity<String> getResponse = authRestTemplate.getForEntity("/api/v1/stores/" + storeId, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
	}
}
