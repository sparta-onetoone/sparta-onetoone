package com.eureka.spartaonetoone.store.application.service;

import com.eureka.spartaonetoone.common.exception.CustomException;
import com.eureka.spartaonetoone.store.domain.entity.Store;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.store.application.dto.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dto.StoreResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoreService {

	private final StoreRepository storeRepository;

	public StoreService(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}

	// Store 엔티티를 StoreResponseDto로 변환
	private StoreResponseDto convertEntityToDto(Store store) {
		return StoreResponseDto.builder()
			.id(store.getId())
			.userId(store.getUserId())
			.orderId(store.getOrderId())
			.name(store.getName())
			.state(store.getState())
			.tellNumber(store.getTellNumber())
			.addressId(store.getAddress() != null ? store.getAddress().getId() : null)
			.description(store.getDescription())
			.minOrderPrice(store.getMinOrderPrice())
			.deliveryFee(store.getDeliveryFee())
			.rating(store.getRating())
			.reviewCount(store.getReviewCount())
			.categoryId(store.getCategoryId())
			.createdAt(store.getCreatedAt())
			.updatedAt(store.getUpdatedAt())
			.deletedAt(store.getDeletedAt())
			.build();
	}

	// 가게 등록
	public StoreResponseDto createStore(StoreRequestDto storeRequestDto) {
		Store store = Store.from(storeRequestDto);
		Store savedStore = storeRepository.save(store);
		return convertEntityToDto(savedStore);
	}

	// 특정 가게 조회
	public StoreResponseDto getStoreById(UUID id) {
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new CustomException("STORE_NOT_FOUND", "Store not found", HttpStatus.NOT_FOUND));
		return convertEntityToDto(store);
	}

	// 전체 가게 조회
	public List<StoreResponseDto> getAllStores() {
		List<Store> stores = storeRepository.findAll();
		return stores.stream()
			.map(this::convertEntityToDto)
			.collect(Collectors.toList());
	}

	// 가게 수정
	public StoreResponseDto updateStore(UUID id, StoreRequestDto storeRequestDto) {
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new CustomException("STORE_NOT_FOUND", "Store not found", HttpStatus.NOT_FOUND));
		store.updateFrom(storeRequestDto);
		Store updatedStore = storeRepository.save(store);
		return convertEntityToDto(updatedStore);
	}

	// 가게 삭제
	public void deleteStore(UUID id) {
		Store store = storeRepository.findById(id)
			.orElseThrow(() -> new CustomException("STORE_NOT_FOUND", "Store not found", HttpStatus.NOT_FOUND));
		storeRepository.delete(store);
	}
}
