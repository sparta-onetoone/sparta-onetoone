package com.eureka.spartaonetoone.store.presentation.controller;

import com.eureka.spartaonetoone.store.application.dtos.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.StoreResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StoreControllerIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	// 인증 정보를 포함한 TestRestTemplate 생성
	private TestRestTemplate getAuthRestTemplate() {
		return restTemplate.withBasicAuth("user", "password");
	}

	// 가게 생성 후 조회 성공 -> Spring Security 활성화 되어 있어서 사용자 정보 인증을 받아와야 한다.
	@Test
	public void testCreateAndGetStoreWithAuth() {
		// 인증 정보가 포함된 TestRestTemplate 생성
		TestRestTemplate authRestTemplate = getAuthRestTemplate();

		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		StoreRequestDto requestDto = new StoreRequestDto(userId, "통합 테스트 가게", "OPEN",
			"010-1111-2222", "통합 테스트 설명", 1000, 500, 4.0f, 5, "생성자", categoryId);

		// POST 요청으로 가게 생성				사용자 정보 인증 넣을 경우
		ResponseEntity<StoreResponseDto> postResponse = restTemplate.postForEntity("/api/v1/stores", requestDto, StoreResponseDto.class);
		assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		StoreResponseDto createdStore = postResponse.getBody();
		assertThat(createdStore).isNotNull();
		UUID storeId = createdStore.getStoreId();

		// GET 요청으로 생성된 가게 조회
		ResponseEntity<StoreResponseDto> getResponse = restTemplate.getForEntity("/api/v1/stores/" + storeId, StoreResponseDto.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		StoreResponseDto fetchedStore = getResponse.getBody();
		assertThat(fetchedStore).isNotNull();
		assertThat(fetchedStore.getName()).isEqualTo("통합 테스트 가게");
	}

	// 존재하지 않는 가게 ID 조회 시 404 NOT_FOUND 발생
	@Test
	public void testGetStoreNotFound() {
		UUID invalidId = UUID.randomUUID();
		ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/stores/" + invalidId, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	// 테스트 : 전체 가게 조회 (페이지네이션) 성공 케이스
	@Test
	public void testGetAllStores() {
		ResponseEntity<Page> response = restTemplate.getForEntity("/api/v1/stores", Page.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	// 테스트 : 가게 수정 성공 케이스
	@Test
	public void testUpdateStoreSuccess() {
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		StoreRequestDto createDto = new StoreRequestDto(userId, "기존 가게", "OPEN",
			"010-1234-5678", "기존 설명", 1000, 500, 4.5f, 10, "생성자", categoryId);
		ResponseEntity<StoreResponseDto> postResponse = restTemplate.postForEntity("/api/v1/stores", createDto, StoreResponseDto.class);
		UUID storeId = postResponse.getBody().getStoreId();

		// 수정 요청 DTO 생성 : "수정된 가게"로 변경
		StoreRequestDto updateDto = new StoreRequestDto(userId, "수정된 가게", "CLOSED",
			"010-9999-8888", "수정된 설명", 1500, 700, 3.5f, 5, "수정자", categoryId);
		restTemplate.put("/api/v1/stores/" + storeId, updateDto);

		// 수정된 가게 조회
		ResponseEntity<StoreResponseDto> getResponse = restTemplate.getForEntity("/api/v1/stores/" + storeId, StoreResponseDto.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		StoreResponseDto updatedStore = getResponse.getBody();
		assertThat(updatedStore.getName()).isEqualTo("수정된 가게");
		assertThat(updatedStore.getState()).isEqualTo("CLOSED");
	}

	// 테스트 : 가게 삭제 성공 케이스
	@Test
	public void testDeleteStore() {
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		StoreRequestDto createDto = new StoreRequestDto(userId, "삭제할 가게", "OPEN",
			"010-1234-5678", "삭제할 설명", 1000, 500, 4.5f, 10, "생성자", categoryId);
		ResponseEntity<StoreResponseDto> postResponse = restTemplate.postForEntity("/api/v1/stores", createDto, StoreResponseDto.class);
		UUID storeId = postResponse.getBody().getStoreId();

		// DELETE 요청으로 soft delete
		restTemplate.delete("/api/v1/stores/" + storeId);

		// 삭제 후, GET 요청 시 404 NOT_FOUND 상태 확인
		ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/v1/stores/" + storeId, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
