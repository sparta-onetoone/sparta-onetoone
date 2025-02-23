package com.eureka.spartaonetoone.store.presentation;

import com.eureka.spartaonetoone.store.application.StoreService;
import com.eureka.spartaonetoone.store.application.dtos.request.StoreRequestDto;
import com.eureka.spartaonetoone.store.application.dtos.response.StoreResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

	private final StoreService storeService;

	// 생성자 주입
	public StoreController(StoreService storeService) {
		this.storeService = storeService;
	}

	// 가게 등록
	@PostMapping
	@Secured("OWNER")
	public ResponseEntity<StoreResponseDto> createStore(@Valid @RequestBody StoreRequestDto storeRequestDto) {
		StoreResponseDto responseDto = storeService.createStore(storeRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 특정 가게 조회
	@GetMapping("/{store_id}")
	@Secured({"CUSTOMER", "OWNER", "ADMIN"})
	public ResponseEntity<StoreResponseDto> getStore(@PathVariable(name = "store_id") UUID storeId) {
		StoreResponseDto responseDto = storeService.getStoreById(storeId);
		return ResponseEntity.ok(responseDto);
	}


	// 전체 가게 목록 조회
	@GetMapping
	@Secured({"CUSTOMER", "OWNER", "ADMIN"})
	public ResponseEntity<Page<StoreResponseDto>> getAllStores(Pageable pageable) {
		Page<StoreResponseDto> responseDtos = storeService.getAllStores(pageable);
		return ResponseEntity.ok(responseDtos);
	}

	// 가게 수정
	@PutMapping("/{store_id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OWNER') and (#storeId == #userDetails.userId)")
	public ResponseEntity<StoreResponseDto> updateStore(@PathVariable(name = "store_id") UUID storeId,
		@Valid @RequestBody StoreRequestDto storeRequestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		StoreResponseDto responseDto = storeService.updateStore(storeId, storeRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 가게 삭제
	@DeleteMapping("/{store_id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'OWNER') and (#storeId == #userDetails.userId)")
	public ResponseEntity<Void> deleteStore(@PathVariable(name = "store_id") UUID storeId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		storeService.deleteStore(storeId);
		return ResponseEntity.noContent().build();
	}
}