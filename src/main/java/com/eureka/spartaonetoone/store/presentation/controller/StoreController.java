package com.eureka.spartaonetoone.store.presentation.controller;

import com.eureka.spartaonetoone.store.application.service.StoreService;
import com.eureka.spartaonetoone.store.application.dto.StoreRequestDto;	//
import com.eureka.spartaonetoone.store.application.dto.StoreResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

	private final StoreService storeService;

	public StoreController(StoreService storeService) {
		this.storeService = storeService;
	}

	// 가게 등록
	@PostMapping
	public ResponseEntity<StoreResponseDto> createStore(@RequestBody StoreRequestDto storeRequestDto) {	//
		StoreResponseDto responseDto = storeService.createStore(storeRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 특정 가게 조회
	@GetMapping("/{id}")
	public ResponseEntity<StoreResponseDto> getStore(@PathVariable(name = "id") UUID id) {
		StoreResponseDto responseDto = storeService.getStoreById(id);
		return ResponseEntity.ok(responseDto);
	}		// getStore(@PathVariable(name = "id") UUID id) { // 이렇게 수정 - 스프링이 인식할 수 있도록 / requesst패럴? 찾아보기 / 링크 참고
	// https://teamsparta.notion.site/RequestParam-PathVariable-Autowired-335787e51fac41418598434fa7f7ed51


	// 전체 가게 목록 조회
	@GetMapping
	public ResponseEntity<List<StoreResponseDto>> getAllStores() {
		List<StoreResponseDto> responseDtos = storeService.getAllStores();
		return ResponseEntity.ok(responseDtos);
	}

	// 가게 수정
	@PutMapping("/{id}")
	public ResponseEntity<StoreResponseDto> updateStore(@PathVariable(name = "id") UUID id, @RequestBody StoreRequestDto storeRequestDto) {
		StoreResponseDto responseDto = storeService.updateStore(id, storeRequestDto);
		return ResponseEntity.ok(responseDto);
	}

	// 가게 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStore(@PathVariable(name = "id") UUID id) {
		storeService.deleteStore(id);
		return ResponseEntity.noContent().build();
	}
}
