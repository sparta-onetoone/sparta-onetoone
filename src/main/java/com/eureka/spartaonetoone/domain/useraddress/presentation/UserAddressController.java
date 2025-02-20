package com.eureka.spartaonetoone.domain.useraddress.presentation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.useraddress.application.UserAddressService;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.response.UserAddressResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class UserAddressController {

	private final UserAddressService userAddressService;

	// 사용자 주소 조회
	@GetMapping("/{user_id}")
	public ResponseEntity<CommonResponse<Page<UserAddressResponseDto>>> getAddressesByUser(
		@PathVariable("user_id") UUID userId,
		@RequestParam(defaultValue = "0") int page, // 페이지 번호
		@RequestParam(defaultValue = "10") int size // 한 페이지에 몇 개 가져올지
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<UserAddressResponseDto> addresses = userAddressService.getAddressesByUser(userId, pageable);

		return ResponseEntity.ok(CommonResponse.success(addresses, "사용자 주소 조회 성공"));
	}

	// 주소 추가
	@PostMapping("/{user_id}")
	public ResponseEntity<CommonResponse<UserAddressResponseDto>> addAddress(
		@PathVariable("user_id") UUID userId,
		@Valid @RequestBody UserAddressRequestDto request) {

		UserAddressResponseDto addedAddress = userAddressService.addAddress(userId, request);
		return ResponseEntity.ok(CommonResponse.success(addedAddress, "주소 추가 성공"));
	}

	// 주소 삭제
	@DeleteMapping("/{address_id}")
	public ResponseEntity<CommonResponse<UserAddressResponseDto>> deleteAddress(
		@PathVariable("address_id") UUID addressId) {

		UserAddressResponseDto deletedAddress = userAddressService.deleteAddress(addressId);
		return ResponseEntity.ok(CommonResponse.success(deletedAddress, "주소 삭제 성공"));
	}

	// 주소 수정
	@PutMapping("/{address_id}")
	public ResponseEntity<CommonResponse<UserAddressResponseDto>> updateAddress(
		@PathVariable("address_id") UUID addressId,
		@Valid @RequestBody UserAddressRequestDto request) {

		UserAddressResponseDto updatedAddress = userAddressService.updateAddress(addressId, request);
		return ResponseEntity.ok(CommonResponse.success(updatedAddress, "주소 수정 성공"));
	}
}