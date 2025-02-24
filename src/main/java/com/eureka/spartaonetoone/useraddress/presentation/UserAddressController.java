package com.eureka.spartaonetoone.useraddress.presentation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.useraddress.application.UserAddressService;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressSearchRequestDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.response.UserAddressResponseDto;

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

	@PostMapping("/{user_id}")
	public ResponseEntity<CommonResponse<UserAddressResponseDto>> addAddress(
		@PathVariable("user_id") UUID userId,
		@Valid @RequestBody UserAddressRequestDto request) {

		try {
			UserAddressResponseDto addedAddress = userAddressService.addAddress(userId, request);
			return ResponseEntity.ok(CommonResponse.success(addedAddress, "주소 추가 성공"));
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주소 추가 실패", e);
		}
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
	//주소 검색
	@GetMapping("/{userId}/search")
	public ResponseEntity<CommonResponse<Page<UserAddressResponseDto>>> searchUserAddresses(
		@PathVariable UUID userId,
		@ModelAttribute UserAddressSearchRequestDto request,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<UserAddressResponseDto> addresses = userAddressService.searchUserAddresses(userId, request, pageable);
		return ResponseEntity.ok(CommonResponse.success(addresses, "주소 검색 성공"));
	}
}
