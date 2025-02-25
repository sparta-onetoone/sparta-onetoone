package com.eureka.spartaonetoone.useraddress.presentation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.response.UserAddressResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "사용자 주소 API", description = "사용자 주소 관리 API")
public interface UserAddressApi {

	@Operation(summary = "사용자 주소 조회", description = "사용자의 주소 목록을 조회하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 주소 조회 성공",
			content = @Content(schema = @Schema(implementation = Page.class)))
	})
	ResponseEntity<CommonResponse<Page<UserAddressResponseDto>>> getAddressesByUser(UUID userId, int page, int size);

	@Operation(summary = "주소 추가", description = "새로운 주소를 추가하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주소 추가 성공",
			content = @Content(schema = @Schema(implementation = UserAddressResponseDto.class)))
	})
	ResponseEntity<CommonResponse<UserAddressResponseDto>> addAddress(UUID userId, UserAddressRequestDto request);

	@Operation(summary = "주소 삭제", description = "주소를 삭제하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주소 삭제 성공",
			content = @Content(schema = @Schema(implementation = UserAddressResponseDto.class)))
	})
	ResponseEntity<CommonResponse<UserAddressResponseDto>> deleteAddress(UUID addressId);

	@Operation(summary = "주소 수정", description = "기존 주소를 수정하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주소 수정 성공",
			content = @Content(schema = @Schema(implementation = UserAddressResponseDto.class)))
	})
	ResponseEntity<CommonResponse<UserAddressResponseDto>> updateAddress(UUID addressId, UserAddressRequestDto request);
}

