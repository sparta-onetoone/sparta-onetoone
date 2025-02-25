package com.eureka.spartaonetoone.user.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.user.application.dtos.request.UserSearchRequestDto;
import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDeleteResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserListResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserSearchResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserUpdateResponseDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "회원 API", description = "회원 관련 API")
public interface UserApi {

	@Operation(summary = "회원 단건 조회", description = "특정 회원을 조회하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 조회 성공",
			content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
	})
	ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserDetail(UUID userId, UserDetailsImpl userDetails);

	@Operation(summary = "회원 목록 조회", description = "회원 목록을 조회하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 목록 조회 성공",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserListResponseDto.class))))
	})
	ResponseEntity<CommonResponse<Page<UserListResponseDto>>> getAllUsers(int page, int size, UserDetailsImpl userDetails);

	@Operation(summary = "회원 수정", description = "회원 정보를 수정하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 수정 성공",
			content = @Content(schema = @Schema(implementation = UserUpdateResponseDto.class)))
	})
	ResponseEntity<CommonResponse<UserUpdateResponseDto>> updateUser(UUID userId, UserUpdateRequestDto requestDto, UserDetailsImpl userDetails);

	@Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
			content = @Content(schema = @Schema(implementation = Json.class)))
	})
	ResponseEntity<CommonResponse<UserDeleteResponseDto>> deleteUser(UUID userId, UserDetailsImpl userDetails);

	@Operation(summary = "회원 검색", description = "회원 정보를 검색하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 검색 성공",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserSearchResponseDto.class))))
	})
	ResponseEntity<CommonResponse<Page<UserSearchResponseDto>>> searchUsers(UserSearchRequestDto requestDto, int page, int size, UserDetailsImpl userDetails);
}
