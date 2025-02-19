package com.eureka.spartaonetoone.domain.useraddress.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.useraddress.application.UserAddressService;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.response.UserAddressResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses/")
public class UserAddressController {

	private final UserAddressService userAddressService;

	// 사용자 주소 조회 API
	@GetMapping("/{user_id}")
	public ResponseEntity<CommonResponse<List<UserAddressResponseDto>>> getAddressesByUser(
		@PathVariable("user_id") UUID userId) {
		List<UserAddressResponseDto> addresses = userAddressService.getAddressesByUser(userId);
		return ResponseEntity.ok(
			CommonResponse.success(addresses, "사용자 주소 조회 성공")
		);
	}

	// 주소 추가 API
	@PostMapping("/{user_id}")
	public ResponseEntity<CommonResponse<UserAddressResponseDto>> addAddress(
		@PathVariable("user_id") UUID userId,
		@RequestBody UserAddressRequestDto request) {
		UserAddressResponseDto addedAddress = userAddressService.addAddress(userId, request);
		return ResponseEntity.ok(
			CommonResponse.success(addedAddress, "주소 추가 성공")
		);
	}

	// 주소 삭제 API (논리적 삭제)
	@DeleteMapping("/{address_id}")
	public ResponseEntity<CommonResponse<UserAddressResponseDto>> deleteAddress(
		@PathVariable("address_id") UUID addressId) {
		UserAddressResponseDto deletedAddress = userAddressService.deleteAddress(addressId);
		return ResponseEntity.ok(
			CommonResponse.success(deletedAddress, "주소 삭제 성공")
		);
	}
}