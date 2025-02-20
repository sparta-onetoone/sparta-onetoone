package com.eureka.spartaonetoone.store.application.service;

import com.eureka.spartaonetoone.common.client.ReviewClient;
import com.eureka.spartaonetoone.common.client.dto.ReviewRequest;
import com.eureka.spartaonetoone.common.client.dto.ReviewResponse;
import com.eureka.spartaonetoone.store.application.exception.StoreException;
import com.eureka.spartaonetoone.store.domain.entity.Store;
import com.eureka.spartaonetoone.store.domain.entity.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.store.application.dto.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dto.StoreResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

// 테스트 : 가게 등록 성공
// 테스트 : 존재하지 않는 가게 ID 조회 시 예외 발생
// 테스트 : 전체 가게 조회 (페이지네이션) 성공
// 테스트 : 가게 수정 성공
// 테스트 : 가게 삭제 (soft delete) 성공

@ExtendWith(MockitoExtension.class)		// Mockito를 사용하여 Mock 객체를 주입하기 위한 JUnit 5 확장
public class StoreServiceTests { // StoreServiceTests  나중에 수정할 것

	@Mock				// 목 객체 생성했고
	private StoreRepository storeRepository;

	@Mock
	private OrderClient orderClient;

	@Mock
	private ReviewClient reviewClient;

	@InjectMocks		// 목으로 생성된 레포지토리 주입
	private StoreService storeService;

	// 가게 등록 성공
	@Test
	public void testCreateStoreSuccess() {
		// given
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		StoreRequestDto requestDto = new StoreRequestDto(
			userId, "테스트 가게", "OPEN", "010-1234-5678",
			"테스트 설명", 1000, 500, 4.5f, 10, "생성자", categoryId
		);
		Store store = Store.of(userId, "테스트 가게", StoreState.OPEN, "010-1234-5678",
			"테스트 설명", 1000, 500, 4.5f, 10, categoryId);
		when(storeRepository.save(any(Store.class))).thenReturn(store);


		// when : createStore 메서드 호출
		StoreResponseDto response = storeService.createStore(requestDto);


		// then : 반환된 DTO의 값 검증
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo("테스트 가게");
		assertThat(response.getState()).isEqualTo("OPEN");
		verify(storeRepository, times(1)).save(any(Store.class));
	}

	// 임의의 아이디, 값을 날렸을때, 입센션을 캐치하는지 발견
	// 존재하지 않는 가게 ID 조회 시 예외 발생
	// 비즈니스 로직을 테스트 ???	지언님 cart 테스트 한번 참고하면 좋다.!!
	@Test
	public void testGetStoreByIdNotFound() {
		// given: 존재하지 않는 가게 ID를 생성하고, repository.findById()가 빈 Optional을 반환하도록 모의
		UUID storeId = UUID.randomUUID();
		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// when & then: StoreNotFoundException이 발생하는지 검증하고, 에러 코드가 "S-001"인지 확인
		assertThatThrownBy(() -> storeService.getStoreById(storeId))
			.isInstanceOf(StoreException.StoreNotFoundException.class)
			.hasMessageContaining("S-001");
	}

	// 전체 가게 조회 (페이지네이션) 성공
	@Test
	public void testGetAllStores() {
		// given
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		Store store = Store.of(userId, "테스트 가게", StoreState.OPEN, "010-1234-5678",
			"테스트 설명", 1000, 500, 4.5f, 10, categoryId);
		Page<Store> page = new PageImpl<>(Arrays.asList(store));
		when(storeRepository.findAll(any(Pageable.class))).thenReturn(page);
		// when
		Page<StoreResponseDto> result = storeService.getAllStores(Pageable.unpaged());
		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	// 가게 수정 성공 (평점과 리뷰 수는 기존 값을 유지)
	@Test
	public void testUpdateStoreSuccess() throws Exception {
		// given
		UUID storeId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		// 기존 가게 생성 (평점 4.5, 리뷰 수 10)
		StoreRequestDto createDto = new StoreRequestDto(
			userId, "기존 가게", "OPEN",
			"010-1234-5678", "기존 설명", 1000, 500, 4.5f, 10, "생성자", categoryId
		);
		Store existingStore = Store.of(userId, "기존 가게", StoreState.OPEN, "010-1234-5678",
			"기존 설명", 1000, 500, 4.5f, 10, categoryId);
		// 리플렉션을 통해 private id 필드에 storeId 주입
		Field idField = Store.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(existingStore, storeId);

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(existingStore));
		when(storeRepository.save(existingStore)).thenReturn(existingStore);

		// 리뷰 집계 업데이트 모의 : 리뷰가 없는 경우
		// 평점과 리뷰 수는 기존 값 그대로 유지되어야 함
		// lenient().when(reviewClient.getReview(any(ReviewRequest.class)))
		// 	.thenReturn(new ReviewResponse(0, existingStore.getRating()));

		// 수정 DTO 생성 : 평점과 리뷰 수는 미입력 (null)
		StoreRequestDto updateDto = new StoreRequestDto(
			userId, "수정된 가게", "CLOSED",
			"010-9999-8888", "수정된 설명", 1500, 700, null, null, "수정자", categoryId
		);
		// when
		StoreResponseDto updatedResponse = storeService.updateStore(storeId, updateDto);
		// then
		assertThat(updatedResponse).isNotNull();
		assertThat(updatedResponse.getName()).isEqualTo("수정된 가게");
		assertThat(updatedResponse.getState()).isEqualTo("CLOSED");
		// 평점과 리뷰 수는 기존 값 그대로 유지되어야 함
		assertThat(updatedResponse.getRating()).isEqualTo(4.5f);
		assertThat(updatedResponse.getReviewCount()).isEqualTo(10);
	}

	// 가게 삭제 성공
	@Test
	public void testDeleteStore() {
		// given
		UUID storeId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		Store store = Store.of(userId, "삭제할 가게", StoreState.OPEN, "010-1234-5678",
			"설명", 1000, 500, 4.5f, 10, categoryId);
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(storeRepository.save(store)).thenReturn(store);
		// when
		storeService.deleteStore(storeId);
		// then: soft delete 후 deletedAt 필드가 설정되어야 함
		assertThat(store.getDeletedAt()).isNotNull();
	}

	// 기존 테스트들은 생략하고, 여기서는 리뷰 집계 업데이트를 검증하는 테스트 케이스입니다.
	// 리뷰 집계 업데이트 성공
	@Test
	public void testUpdateStoreReview() throws Exception {
		// given
		UUID storeId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();
		// 기존 가게 생성 (평점 4.5, 리뷰 수 10)
		Store store = Store.of(userId, "집계 테스트 가게", StoreState.OPEN, "010-0000-0000",
			"집계 테스트 설명", 1000, 500, 4.5f, 10, categoryId);
		Field idField = Store.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(store, storeId);

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		when(storeRepository.save(store)).thenReturn(store);

		// OrderClient 모의: 해당 가게에 두 개의 주문이 있다고 가정
		OrderResponse order1 = new OrderResponse(UUID.randomUUID());
		OrderResponse order2 = new OrderResponse(UUID.randomUUID());
		List<OrderResponse> orders = Arrays.asList(order1, order2);
		when(orderClient.getOrders(storeId)).thenReturn(orders);

		// ReviewClient 모의: 두 주문에 대해 각각 한 건의 리뷰가 있고, 평점은 4와 5라 가정
		ReviewResponse review1 = new ReviewResponse(4);
		ReviewResponse review2 = new ReviewResponse(5);
		List<ReviewResponse> reviews = Arrays.asList(review1, review2);
		when(reviewClient.getReviews(any())).thenReturn(reviews);

		// when: 리뷰 집계 업데이트 메서드 호출
		storeService.updateStoreReview(storeId);

		// then: 가게의 평점과 리뷰 수가 외부 API 응답값으로 업데이트 되었는지 검증
		assertThat(store.getRating()).isEqualTo(4.0f);
		assertThat(store.getReviewCount()).isEqualTo(3);
	}

}
