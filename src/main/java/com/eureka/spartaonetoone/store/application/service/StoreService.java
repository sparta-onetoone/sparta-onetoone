package com.eureka.spartaonetoone.store.application.service;

import com.eureka.spartaonetoone.common.client.ReviewAggregateClient;
import com.eureka.spartaonetoone.common.client.dto.ReviewAggregateRequest;
import com.eureka.spartaonetoone.common.client.dto.ReviewAggregateResponse;
import com.eureka.spartaonetoone.store.application.exception.StoreException;
import com.eureka.spartaonetoone.store.domain.entity.Store;
import com.eureka.spartaonetoone.store.domain.entity.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.store.application.dto.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dto.StoreResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService {

	private final StoreRepository storeRepository;
	private final ReviewAggregateClient reviewAggregateClient; // 리뷰 집계 업데이트용

	// 생성자 주입: StoreRepository를 통해 데이터베이스 접근을 담당
	public StoreService(StoreRepository storeRepository, ReviewAggregateClient reviewAggregateClient) {
		this.storeRepository = storeRepository;
		this.reviewAggregateClient = reviewAggregateClient;
	}

	// 엔티티 -> DTO 변환 메서드 StoreResponseDto.of() 정적 메서드를 호출하여 Store 엔티티의 데이터를 DTO로 변환
	private StoreResponseDto convertEntityToDto(Store store) {
		return StoreResponseDto.of(store);
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
		return Store.of(
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
		// DTO를 Entity로 변환
		Store store = convertDtoToEntity(dto);
		// Repository를 사용해 Entity 저장
		Store savedStore = storeRepository.save(store);
		// 저장된 Entity를 DTO로 변환하여 반환
		return convertEntityToDto(savedStore);
	}

	// 특정 가게 조회
	public StoreResponseDto getStoreById(UUID storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);
		return convertEntityToDto(store);
	}

	// 전체 가게 조회(페이지네이션 지원) - Pageable을 사용해 Store 엔티티를 페이지 단위로 조회하고, 각 Entity를 DTO로 변환하여 Page 객체로 반환
	public Page<StoreResponseDto> getAllStores(Pageable pageable) {
		return storeRepository.findAll(pageable)
			.map(this::convertEntityToDto);
	}

	// 가게 수정 - 특정 storeId에 해당하는 Entity를 조회한 후, DTO의 값으로 업데이트하고 저장
	public StoreResponseDto updateStore(UUID storeId, StoreRequestDto dto) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);
		// DTO의 값을 Entity에 반영하여 업데이트 (updateFromFields는 엔티티 내부 업데이트 메서드)
		StoreState stateEnum;
		try {
			stateEnum = dto.getState() != null ? StoreState.valueOf(dto.getState().toUpperCase()) : store.getState();
		} catch (IllegalArgumentException e) {
			stateEnum = store.getState();
		}
		store.updateFrom(
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
		return convertEntityToDto(updatedStore);
	}

	// 주어진 storeId로 Entity를 조회한 후, markDeleted()를 호출하여 삭제된 것처럼 표시하고 저장
	public void deleteStore(UUID storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);
		store.markDeleted();
		storeRepository.save(store);
	}

	// 리뷰 집계 업데이트 메서드
	public void updateStoreReviewAggregate(UUID storeId) {
		// ReviewAggregateRequest 생성
		ReviewAggregateRequest request = new ReviewAggregateRequest(storeId);
		// ReviewAggregateClient를 통해 POST 방식으로 리뷰 집계 데이터를 조회 (동기 호출)
		ReviewAggregateResponse response = reviewAggregateClient.getReviewAggregate(request);
		// 응답 받은 값으로 가게 집계 업데이트
		Store store = storeRepository.findById(storeId)
			.orElseThrow(StoreException.StoreNotFoundException::new);

		// 합산하는거 깜빡, 불러오기만 했다...;;

		// 기존 필드 중 이름, state, 전화번호 등은 그대로 유지하고, 평점과 리뷰 수만 업데이트
		store.updateFrom(
			store.getName(),
			store.getState(),
			store.getTellNumber(),
			store.getDescription(),
			store.getMinOrderPrice(),
			store.getDeliveryFee(),
			response.getAverageRating(), // 외부에서 받아온 평균 평점
			response.getReviewCount(),   // 외부에서 받아온 총 리뷰 수
			store.getCategoryId()
		);
		storeRepository.save(store);
	}
}
