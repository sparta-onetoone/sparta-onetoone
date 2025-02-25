package com.eureka.spartaonetoone.store.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.store.application.StoreService;
import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController implements StoreApi {

	private final StoreService storeService;

	public StoreController(StoreService storeService) {
		this.storeService = storeService;
	}

	// 가게 등록
	@PostMapping
	@Secured({"ROLE_OWNER","ROLE_ADMIN"})
	public ResponseEntity<CommonResponse<?>> createStore(@Valid @RequestBody StoreRequestDto storeRequestDto) {
		StoreResponseDto responseDto = storeService.createStore(storeRequestDto);
		return ResponseEntity.ok(CommonResponse.success(responseDto, "가게 등록 성공"));
	}

	// 특정 가게 조회
	@GetMapping("/{store_id}")
	@Secured({"ROLE_CUSTOMER", "ROLE_OWNER", "ROLE_ADMIN"})
	public ResponseEntity<CommonResponse<?>> getStore(@PathVariable("store_id") UUID storeId) {
		StoreResponseDto responseDto = storeService.getStoreById(storeId);
		return ResponseEntity.ok(CommonResponse.success(responseDto, "가게 조회 성공"));
	}

	// 조건에 따른 가게 검색 (페이징)
	@GetMapping("/search")
	public ResponseEntity<CommonResponse<?>> searchStores(
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String name,
			Pageable pageable) {
		Page<StoreResponseDto> responseDtos = storeService.searchStores(category, name, pageable);
		return ResponseEntity.ok(CommonResponse.success(responseDtos, "가게 검색 성공"));
	}

	// 전체 가게 목록 조회 (페이징)
	@GetMapping
	@Secured({"ROLE_CUSTOMER", "ROLE_OWNER", "ROLE_ADMIN"})
	public ResponseEntity<CommonResponse<?>> getAllStores(Pageable pageable) {
		Page<StoreResponseDto> responseDtos = storeService.getAllStores(pageable);
		return ResponseEntity.ok(CommonResponse.success(responseDtos, "전체 가게 조회 성공"));
	}

	// 가게 수정
	@PutMapping("/{store_id}")
	@Secured({"ROLE_OWNER", "ROLE_ADMIN"})
	public ResponseEntity<CommonResponse<?>> updateStore(
			@PathVariable("store_id") UUID storeId,
			@Valid @RequestBody StoreRequestDto storeRequestDto,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		StoreResponseDto responseDto = storeService.updateStore(storeId, storeRequestDto);
		return ResponseEntity.ok(CommonResponse.success(responseDto, "가게 수정 성공"));
	}

	// 가게 삭제
	@DeleteMapping("/{store_id}")
	@Secured({"ROLE_OWNER", "ROLE_ADMIN"})
	public ResponseEntity<CommonResponse<?>> deleteStore(
			@PathVariable("store_id") UUID storeId,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {

		String userRole = userDetails.getAuthorities().stream()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("권한 없음"))
				.getAuthority();
		UUID userId = userDetails.getUserId();

		storeService.deleteStore(userRole, storeId, userId);
		return ResponseEntity.ok(CommonResponse.success(null, "가게 삭제 성공"));
	}
}
