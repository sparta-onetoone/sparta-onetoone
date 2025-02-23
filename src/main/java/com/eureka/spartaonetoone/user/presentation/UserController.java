package com.eureka.spartaonetoone.user.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.user.application.UserService;
import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDeleteResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserListResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserUpdateResponseDto;
import com.eureka.spartaonetoone.user.application.exception.UserException;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	//회원 단건 조회
	@GetMapping("/{user_id}")
	public ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserDetail(
		@PathVariable("user_id") UUID userId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		// 인증된 사용자 정보 확인
		if (userId.equals(userDetails.getUserId())) {
			return ResponseEntity.ok(
				CommonResponse.success(userService.getUserDetail(userId), "회원 정보 상세 조회 성공")
			);
		} else {
			throw new UserException.UserNotFoundException();  // 권한 없으면 예외 처리
		}
	}
	//회원 다건 조회 (페이지네이션)
	@GetMapping()
	public ResponseEntity<CommonResponse<Page<UserListResponseDto>>> getAllUsers(
		@RequestParam(defaultValue = "0") int page, // 페이지 번호
		@RequestParam(defaultValue = "10") int size, // 페이지 크기
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		// Pageable 객체를 생성
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());

		Page<UserListResponseDto> userPage = userService.getAllUsers(pageable);

		return ResponseEntity.ok(
			CommonResponse.success(userPage, "회원 정보 전체 조회 성공")
		);
	}

	//회원 수정
	@PutMapping("/{user_id}")
	public ResponseEntity<CommonResponse<UserUpdateResponseDto>> updateUser(
		@PathVariable("user_id") UUID userId,
		@Valid @RequestBody UserUpdateRequestDto request,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		if (userId.equals(userDetails.getUserId())) {
			return ResponseEntity.ok(
				CommonResponse.success(userService.updateUser(userId, request), "회원 정보 수정 성공")
			);
		} else {
			throw new UserException.UserNotFoundException();  // 권한 없으면 예외 처리
		}
	}

	//회원 탈퇴
	@DeleteMapping("/{user_id}")
	public ResponseEntity<CommonResponse<UserDeleteResponseDto>> deleteUser(
		@PathVariable("user_id") UUID userId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		if (userId.equals(userDetails.getUserId())) {
			return ResponseEntity.ok(
				CommonResponse.success(userService.deleteUser(userId), "회원 탈퇴 성공")
			);
		} else {
			throw new UserException.UserNotFoundException();  // 권한 없으면 예외 처리
		}
	}
}