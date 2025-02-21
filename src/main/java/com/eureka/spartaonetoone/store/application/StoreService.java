package com.eureka.spartaonetoone.store.application;

import com.eureka.spartaonetoone.common.client.OrderClient;
import com.eureka.spartaonetoone.common.client.ReviewClient;
import com.eureka.spartaonetoone.common.dtos.response.ReviewResponse;
import com.eureka.spartaonetoone.common.dtos.response.OrderResponse;
import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.exception.StoreException;
import com.eureka.spartaonetoone.store.domain.Store;
import com.eureka.spartaonetoone.store.domain.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
public class StoreService {

	private final StoreRepository storeRepository;
	private final OrderClient orderClient;
	private final ReviewClient reviewClient;

	// 생성자 주입
	public StoreService(StoreRepository storeRepository, OrderClient orderClient, ReviewClient reviewClient) {
		this.storeRepository = storeRepository;
		this.orderClient = orderClient;
		this.reviewClient = reviewClient;
	}

	// DTO -> 엔티티 변환 메서드 (Service 내부 변환 로직) - StoreRequestDto의 값을 추출하여, Store 엔티티를 생성하는 데 사용
	private Store convertDtoToEntity(StoreRequestDto dto) {
		StoreState stateEnum;
		try {
			// DTO의 state 문자열을 대문자로 변환 후 ENUM으로 변환. 잘못된 값이면 기본값 OPEN 사용.
			stateEnum = dto.getState() != null ? StoreState.valueOf(dto.getState().toUpperCase()) : StoreState.OPEN;
		} catch (IllegalArgumentException e) {
			stateEnum = StoreState.OPEN;
		}
		// Service 계층에서 필요한 필드만 추출하여 엔티티 생성 (순환참조를 피하기 위해 DTO에 직접 의존하지 않음)
		return Store.createStore(
			dto.getUserId(),
			dto.getName(),
			stateEnum,
			dto.getTellNumber(),
			dto.getDescription(),
			dto.getMinOrderPrice() != null ? dto.getMinOrderPrice() : 0,
			dto.getDeliveryFee() != null ? dto.getDeliveryFee() : 0,
			dto.getRating() != null ? dto.getRating() : 0.0f,
			dto.getReviewCount() != null ? dto.getReviewCount() : 0,
			dto.getCategoryId()
		);
	}

	// 가게 등록 - 클라이언트로부터 전달받은 StoreRequestDto를 변환하여 Store 엔티티로 생성하고, 저장한 후 DTO로 반환
	public StoreResponseDto createStore(StoreRequestDto dto) {
		// given : DTO의 state 문자열을 파싱하여 ENUM으로 변환
		StoreState stateEnum = parseStoreState(dto.getState());
		// given : DTO의 데이터를 이용해 Store 엔티티를 생성 (null 값에 대해 기본값 적용)
		Store store = Store.createStore(
			dto.getUserId(),
			dto.getName(),
			stateEnum,
			dto.getTellNumber(),
			dto.getDescription(),
			dto.getMinOrderPrice() != null ? dto.getMinOrderPrice() : 0,
			dto.getDeliveryFee() != null ? dto.getDeliveryFee() : 0,
			dto.getRating() != null ? dto.getRating() : 0.0f,
			dto.getReviewCount() != null ? dto.getReviewCount() : 0,
			dto.getCategoryId()
		);
		// when : 엔티티를 Repository를 통해 저장
		Store savedStore = storeRepository.save(store);
		// then : 저장된 엔티티를 DTO로 변환하여 반환
		return StoreResponseDto.from(savedStore);
	}
	// state 문자열을 ENUM으로 파싱하는 헬퍼 메서드
	private StoreState parseStoreState(String state) {
		try {
			return state != null ? StoreState.valueOf(state.toUpperCase()) : StoreState.OPEN;
		} catch (IllegalArgumentException e) {
			return StoreState.OPEN;
		}
	}

	// 특정 가게 조회
	public StoreResponseDto getStoreById(UUID storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);
		return StoreResponseDto.from(store);
	}

	// 전체 가게 조회(페이지네이션 지원) - Pageable을 사용해 Store 엔티티를 페이지 단위로 조회하고, 각 Entity를 DTO로 변환하여 Page 객체로 반환
	public Page<StoreResponseDto> getAllStores(Pageable pageable) {
		return storeRepository.findAll(pageable)
			.map(StoreResponseDto::from);
	}

	// 가게 수정 - 특정 storeId에 해당하는 Entity를 조회한 후, DTO의 값으로 업데이트하고 저장
	public StoreResponseDto updateStore(UUID storeId, StoreRequestDto dto) {
		// storeId에 해당하는 가게를 조회 (없으면 예외 발생)
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);

		// DTO의 state 값을 대문자로 변환하여 ENUM으로 매핑, 오류 발생 시 기존 값을 사용
		StoreState stateEnum;
		try {
			stateEnum = (dto.getState() != null) ? StoreState.valueOf(dto.getState().toUpperCase()) : store.getState();
		} catch(Exception e) {
			stateEnum = store.getState();
		}
		store.update(
			dto.getName(),
			stateEnum,
			dto.getTellNumber(),
			dto.getDescription(),
			dto.getMinOrderPrice(),
			dto.getDeliveryFee(),
			store.getRating(),
			store.getReviewCount(),
			dto.getCategoryId()
		);
		Store updatedStore = storeRepository.save(store);
		return StoreResponseDto.from(updatedStore);
	}

	// 주어진 storeId로 Entity를 조회한 후, markDeleted()를 호출하여 삭제된 것처럼 표시하고 저장
	@Transactional
	public void deleteStore(UUID storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);
		store.markDeleted();
	}

	// 리뷰 집계 업데이트 메서드
	@Transactional
	public void updateStoreReview(UUID storeId) {
		// 1. OrderClient를 사용하여 해당 가게의 주문 목록 조회
		List<OrderResponse> orders = orderClient.getOrders(storeId);

		// 2. 주문 목록에서 각 주문의 orderId 추출
		List<UUID> orderIds = orders.stream()
			.map(OrderResponse::getOrderId)
			.collect(Collectors.toList());
		// 3. ReviewClient를 통해 해당 orderId 목록에 해당하는 리뷰 데이터 조회
		List<ReviewResponse> reviews = reviewClient.getReviews(orderIds);
		// 4. 총 리뷰 개수와 총 평점 합계 계산
		int reviewCount = reviews.size();
		float totalRating = (float) reviews.stream()
			.mapToDouble(ReviewResponse::getTotalRating)
			.sum();
		float avgRating = reviewCount > 0 ? totalRating / reviewCount : 0.0f;
		// 5. 해당 가게 엔티티 조회 및 업데이트
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);
		// 기존 필드 중 이름, state, 전화번호 등은 그대로 유지하고, 평점과 리뷰 수만 업데이트
		store.update(
			store.getName(),
			store.getState(),
			store.getTellNumber(),
			store.getDescription(),
			store.getMinOrderPrice(),
			store.getDeliveryFee(),
			avgRating, // 외부에서 받아온 평균 평점
			reviewCount,    // 외부에서 받아온 총 리뷰 수
			store.getCategoryId()
		);
	}
}
