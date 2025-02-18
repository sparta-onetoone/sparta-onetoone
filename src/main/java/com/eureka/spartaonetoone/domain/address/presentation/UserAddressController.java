package com.eureka.spartaonetoone.domain.address.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.domain.address.application.UserAddressService;
import com.eureka.spartaonetoone.domain.address.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.domain.address.application.dtos.response.UserAddressResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    // 사용자 주소 조회 API
    @GetMapping("/api/v1/addresses/{user_id}")
    public ResponseEntity<CommonResponse<List<UserAddressResponseDto>>> getAddressesByUser(
            @PathVariable("user_id") UUID userId) {
        List<UserAddressResponseDto> addresses = userAddressService.getAddressesByUser(userId);
        return ResponseEntity.ok(
                CommonResponse.success(addresses, "사용자 주소 조회 성공")
        );
    }

    // 주소 추가 API
    @PostMapping("/api/v1/addresses/{user_id}")
    public ResponseEntity<CommonResponse<UserAddressResponseDto>> addAddress(
            @PathVariable("user_id") UUID userId,
            @RequestBody UserAddressRequestDto request) {
        UserAddressResponseDto addedAddress = userAddressService.addAddress(userId, request);
        return ResponseEntity.ok(
                CommonResponse.success(addedAddress, "주소 추가 성공")
        );
    }

    // 주소 삭제 API (논리적 삭제)
    @DeleteMapping("/api/v1/addresses/{address_id}")
    public ResponseEntity<CommonResponse<UserAddressResponseDto>> deleteAddress(
            @PathVariable("address_id") UUID addressId) {
        UserAddressResponseDto deletedAddress = userAddressService.deleteAddress(addressId);
        return ResponseEntity.ok(
                CommonResponse.success(deletedAddress, "주소 삭제 성공")
        );
    }
}