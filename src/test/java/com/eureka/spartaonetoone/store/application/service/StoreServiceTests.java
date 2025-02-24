package com.eureka.spartaonetoone.store.application.service;

import com.eureka.spartaonetoone.category.domain.Category;
import com.eureka.spartaonetoone.common.client.CategoryClient;
import com.eureka.spartaonetoone.common.client.OrderClient;
import com.eureka.spartaonetoone.common.client.ReviewClient;
import com.eureka.spartaonetoone.common.dtos.response.ReviewResponse;
import com.eureka.spartaonetoone.common.dtos.response.OrderResponse;
import com.eureka.spartaonetoone.store.application.StoreService;
import com.eureka.spartaonetoone.store.application.exception.StoreException;
import com.eureka.spartaonetoone.store.domain.Store;
import com.eureka.spartaonetoone.store.domain.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

// 테스트 : 가게 등록 성공
// 테스트 : 존재하지 않는 가게 ID 조회 시 예외 발생
// 테스트 : 전체 가게 조회(페이지네이션) 성공
// 테스트 : 가게 수정 성공
// 테스트 : 가게 삭제 성공
// 테스트 : 가게 삭제 실패
// 테스트 : 리뷰 집계 업데이트

@ExtendWith(MockitoExtension.class)		// Mockito를 사용하여 Mock 객체를 주입하기 위한 JUnit 5 확장
public class StoreServiceTests { // StoreServiceTests  나중에 수정할 것

	@Mock
	private StoreRepository storeRepository;
	@Mock
	private OrderClient orderClient;
	@Mock
	private ReviewClient reviewClient;
	@Mock
	private CategoryClient categoryClient;
	@InjectMocks		// 목으로 생성된 레포지토리 주입
	private StoreService storeService;

	// 가게 등록 성공 테스트 (카테고리 포함)
	@Test
	public void testCreateStoreSuccess() {
		// given: 가게 등록 요청 데이터 생성
		UUID userId = UUID.randomUUID();
		List<String> categoryIds = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

		StoreRequestDto requestDto = new StoreRequestDto(
				userId, "테스트 가게", "OPEN", "010-1234-5678",
				"테스트 설명", 1500, 700, 4.5f, 10, "생성자", categoryIds
		);

		// CategoryClient가 반환할 가짜 카테고리 객체 생성
		List<Category> mockCategories = categoryIds.stream()
				.map(id -> Category.of("테스트 카테고리"))
				.collect(Collectors.toList());
		when(categoryClient.getCategoryByIds(requestDto.getCategoryIds())).thenReturn(mockCategories);

		// 예상되는 categoryIds 문자열 (쉼표로 구분된 UUID)
		Store store = Store.createStore(userId, "테스트 가게", StoreState.OPEN, "010-1234-5678",
				"테스트 설명", 1500, 700, 4.5f, 10, categoryIds);
		when(storeRepository.save(any(Store.class))).thenReturn(store);
		// when : createStore 메서드 호출
		StoreResponseDto response = storeService.createStore(requestDto);
		// then : 반환된 DTO의 값 검증
		assertThat(response).isNotNull();
		assertThat(response.getName()).isEqualTo("테스트 가게");
		assertThat(response.getState()).isEqualTo("OPEN");
		assertThat(response.getCategoryIds()).containsExactlyElementsOf(categoryIds);
		// storeRepository가 1번 호출되었는지 확인
		verify(storeRepository, times(1)).save(any(Store.class));
		// categoryClient가 1번 호출되었는지 확인
		verify(categoryClient, times(1)).getCategoryByIds(requestDto.getCategoryIds());
	}

	// 잘못된 카테고리로 가게 등록 시 예외 발생 테스트
	@Test
	public void testCreateStoreWithInvalidCategory() {
		// given
		UUID userId = UUID.randomUUID();
		List<String> invalidCategoryIds = List.of(UUID.randomUUID().toString()); // 존재하지 않는 카테고리 ID

		StoreRequestDto requestDto = new StoreRequestDto(
				userId, "잘못된 카테고리 가게", "OPEN", "010-0000-0000",
				"테스트 설명", 2000, 800, 4.0f, 5, "생성자", invalidCategoryIds
		);
		when(categoryClient.getCategoryByIds(requestDto.getCategoryIds()))
				.thenThrow(new StoreException("RC-2222","Category not found", HttpStatus.NOT_FOUND));

		// when & then
		assertThatThrownBy(() -> storeService.createStore(requestDto))
				.isInstanceOf(StoreException.class)
				.hasMessageContaining("Category not found");
	}

	@Test
	public void testSearchStoresByCategoryName() {
		// given
		UUID userId = UUID.randomUUID();
		List<String> categoryIds = List.of(UUID.randomUUID().toString());
		Store store = Store.createStore(userId, "검색 테스트 가게", StoreState.OPEN, "010-2222-3333",
				"테스트 설명", 1000, 500, 4.5f, 12, categoryIds);

		Page<Store> page = new PageImpl<>(List.of(store));
		when(storeRepository.findAll(any(BooleanExpression.class), any(Pageable.class))).thenReturn(page);
		// when
		Page<StoreResponseDto> result = storeService.searchStores(categoryIds.get(0), null, Pageable.unpaged());
		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

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
		Store store = Store.createStore(userId, "테스트 가게", StoreState.OPEN, "010-1234-5678",
			"테스트 설명", 1000, 500, 4.5f, 10, List.of("uuid1","uuid2"));
		Page<Store> page = new PageImpl<>(Collections.singletonList(store));
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
			"010-1234-5678", "기존 설명", 1000, 500, 4.5f, 10, "생성자", List.of("uuid1", "uuid2")
		);
		Store existingStore = Store.createStore(userId, "기존 가게", StoreState.OPEN, "010-1234-5678",
			"기존 설명", 1000, 500, 4.5f, 10, List.of("uuid1", "uuid2"));
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
			"010-9999-8888", "수정된 설명", 1500, 700, null, null, "수정자", List.of("uuid1", "uuid2")
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
	public void testDeleteStore_AsAdmin_Success() {
		// given: ADMIN 권한을 가진 사용자
		UUID storeId = UUID.randomUUID();
		UUID adminId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID(); // 가게 소유자
		List<String> categoryIds = List.of(UUID.randomUUID().toString());

		Store store = Store.createStore(ownerId, "삭제할 가게", StoreState.OPEN, "010-1234-5678",
				"설명", 1000, 500, 4.5f, 10, categoryIds);
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

		// when: ADMIN이 삭제 요청
		storeService.deleteStore("ADMIN", storeId, adminId);
		// then: 삭제 후 deletedAt 필드가 설정되어 있는지 확인
		assertThat(store.getDeletedAt()).isNotNull();
	}
	// 가게 삭제 실패 (다른 사용자 가게 삭제 시도)
	@Test
	public void testDeleteStore_AsOwner_Fail() {
		// given
		UUID storeId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();
		UUID otherUserId = UUID.randomUUID();
		List<String> categoryIds = List.of(UUID.randomUUID().toString());

		Store store = Store.createStore(ownerId, "삭제할 가게", StoreState.OPEN, "010-1234-5678",
				"설명", 1000, 500, 4.5f, 10, categoryIds);
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

		// when & then
		assertThatThrownBy(() -> storeService.deleteStore("OWNER", storeId, otherUserId))
				.isInstanceOf(StoreException.NoPermissionToDeleteException.class);
	}

	// 기존 테스트들은 생략하고, 여기서는 리뷰 집계 업데이트를 검증하는 테스트 케이스입니다.
	// 리뷰 집계 업데이트 성공
	@Test
	@Transactional
	public void testUpdateStoreReview() throws Exception {
		// given: 기존 가게 생성 (초기 평점 4.5, 리뷰 수 10)
		UUID storeId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		List<String> categoryIds = List.of(UUID.randomUUID().toString());
		Store store = Store.createStore(userId, "집계 테스트 가게", StoreState.OPEN, "010-0000-0000",
				"집계 테스트 설명", 1000, 500, 4.5f, 10, categoryIds);

		Field idField = Store.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(store, storeId);

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		// (불필요한 storeRepository.save() stubbing은 제거)

		// given: OrderClient 모의 - 해당 가게에 두 개의 주문이 있다고 가정
		OrderResponse order1 = new OrderResponse(UUID.randomUUID());
		OrderResponse order2 = new OrderResponse(UUID.randomUUID());
		List<OrderResponse> orders = Arrays.asList(order1, order2);
		when(orderClient.getOrders(storeId)).thenReturn(orders);

		ReviewResponse review1 = new ReviewResponse(4, 4);
		ReviewResponse review2 = new ReviewResponse(5, 5);
		List<ReviewResponse> reviews = Arrays.asList(review1, review2);
		when(reviewClient.getReviews(any())).thenReturn(reviews);

		// when
		storeService.updateStoreReview(storeId);

		// then: 가게의 평점과 리뷰 수가 외부 리뷰 데이터에 따라 업데이트 되었는지 검증
		// 총 리뷰 수: 2, 총 평점 합계: 9, 평균 평점: 9/2 = 4.5
		assertThat(store.getRating()).isEqualTo(4.5f);
		assertThat(store.getReviewCount()).isEqualTo(2);
	}
}
