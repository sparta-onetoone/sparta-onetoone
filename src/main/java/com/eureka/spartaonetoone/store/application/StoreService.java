package com.eureka.spartaonetoone.store.application;

import com.eureka.spartaonetoone.category.domain.Category;
import com.eureka.spartaonetoone.common.client.CategoryClient;
import com.eureka.spartaonetoone.common.client.OrderClient;
import com.eureka.spartaonetoone.common.client.ReviewClient;
import com.eureka.spartaonetoone.common.dtos.response.OrderResponse;
import com.eureka.spartaonetoone.common.dtos.response.ReviewResponse;
import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.eureka.spartaonetoone.store.application.exception.StoreException;
import com.eureka.spartaonetoone.store.domain.QStore;
import com.eureka.spartaonetoone.store.domain.Store;
import com.eureka.spartaonetoone.store.domain.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.category.domain.repository.CategoryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // 생성자 주입 위에 주석 참고
public class StoreService {

    private final StoreRepository storeRepository;
    private final OrderClient orderClient;
    private final ReviewClient reviewClient;
    private final CategoryClient categoryClient;


    // 가게 등록 - DTO를 엔티티로 변환하고, 가게 정보를 데이터베이스에 저장합니다.
    @Transactional
    public StoreResponseDto createStore(StoreRequestDto dto) {
        List<Category> categories = categoryClient.getCategoryByIds(dto.getCategoryIds());
        List<String> categoryIds = categories.stream()
                .map(Category::getId) // Category 엔티티에서 ID(UUID) 가져오기
                .map(UUID::toString)  // UUID를 String으로 변환
                .collect(Collectors.toUnmodifiableList()); // 쉼표(,)로 구분된 문자열로 변환

        Store store = Store.createStore(
                dto.getUserId(),
                dto.getName(),
                parseStoreState(dto.getState()),
                dto.getTellNumber(),
                dto.getDescription(),
                dto.getMinOrderPrice() != null ? dto.getMinOrderPrice() : 0,
                dto.getDeliveryFee() != null ? dto.getDeliveryFee() : 0,
                dto.getRating() != null ? dto.getRating() : 0.0f,
                dto.getReviewCount() != null ? dto.getReviewCount() : 0,
                categoryIds
        );
        Store savedStore = storeRepository.save(store);
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

    // 특정 가게 조회 - 주어진 ID로 가게를 조회합니다. 가게가 존재하지 않을 경우 예외를 발생시킵니다.
    public StoreResponseDto getStoreById(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreException.StoreNotFoundException::new);
        return StoreResponseDto.from(store);
    }

    public Page<StoreResponseDto> searchStores(String categoryName, String storeName, Pageable pageable) {
        QStore store = QStore.store;
        BooleanExpression predicate = store.isDeleted.isFalse();

        if (categoryName != null) {predicate = predicate.and(store.categoryIds.contains(categoryName));}
        if (storeName != null) {predicate = predicate.and(store.name.containsIgnoreCase(storeName));}
        return storeRepository.findAll(predicate, pageable).map(StoreResponseDto::from);
    }

    // 전체 가게 조회(페이지네이션 지원) - Pageable을 사용해 Store 엔티티를 페이지 단위로 조회하고, 각 Entity를 DTO로 변환하여 Page 객체로 반환
    public Page<StoreResponseDto> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable).map(StoreResponseDto::from);
    }

    // 가게 수정 - 특정 storeId에 해당하는 Entity를 조회한 후, DTO의 값으로 업데이트하고 저장
    @Transactional
    public StoreResponseDto updateStore(UUID storeId, @Valid StoreRequestDto dto) {
        // storeId에 해당하는 가게를 조회 (없으면 예외 발생)
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreException.StoreNotFoundException::new);
        StoreState stateEnum;
        try {
            stateEnum = (dto.getState() != null) ? StoreState.valueOf(dto.getState().toUpperCase()) : store.getState();
        } catch (Exception e) {
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
                store.getReviewCount()
        );
        Store updatedStore = storeRepository.save(store);
        return StoreResponseDto.from(updatedStore);
    }

    @Transactional
    public void deleteStore(String userRole, UUID storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreException.StoreNotFoundException::new);
        // 'OWNEW'인 경우, 본인이 생성한 가게만 삭제 가능
        if (userRole.equals("OWNER") && !store.getUserId().equals(userId)) {
            throw new StoreException.NoPermissionToDeleteException();
        }
        // 'ADMIN'은 모든 가게 삭제 가능
        if (!userRole.equals("ADMIN") && !userRole.equals("OWNER")) {
            throw new StoreException.NoPermissionToDeleteException();
        }
        store.softDelete(userId);
        storeRepository.save(store);
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
                reviewCount    // 외부에서 받아온 총 리뷰 수
        );
    }
}
