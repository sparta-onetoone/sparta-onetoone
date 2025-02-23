package com.eureka.spartaonetoone.user.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.user.application.dtos.request.UserUpdateRequestDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDeleteResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserDetailResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserListResponseDto;
import com.eureka.spartaonetoone.user.application.dtos.response.UserUpdateResponseDto;
import com.eureka.spartaonetoone.user.application.exception.UserException;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	// 회원 상세 조회 서비스 로직
	public UserDetailResponseDto getUserDetail(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserException.UserNotFoundException::new); // 사용자 없음 예외 처리
		if (user.getIsDeleted()) {
			throw new UserException.DeletedUserAccessException(); // 삭제된 사용자 접근 예외 처리
		}
		return UserDetailResponseDto.from(user);
	}

	// 회원 전체 조회 서비스 로직 (삭제되지 않은 사용자만 조회) - 페이지네이션 적용
	public Page<UserListResponseDto> getAllUsers(Pageable pageable) {
		// Page<User>를 받아서 Page<UserListResponseDto>로 변환
		Page<User> userPage = userRepository.findAll(pageable);

		// Page<User>를 Page<UserListResponseDto>로 변환하여 반환
		return userPage.map(UserListResponseDto::from);
	}

	// 회원 수정 서비스 로직
	@Transactional
	public UserUpdateResponseDto updateUser(UUID userId, UserUpdateRequestDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserException.UserNotFoundException::new); // 사용자 없음 예외 처리

		if (user.getIsDeleted()) {
			throw new UserException.DeletedUserAccessException(); // 삭제된 사용자 접근 예외 처리
		}

		// 사용자 정보 업데이트
		user.updateUsername(request.getUsername());
		user.updateEmail(request.getEmail());

		return UserUpdateResponseDto.builder()
			.userId(user.getUserId()) // UUID 그대로 유지
			.username(user.getUsername())
			.email(user.getEmail())
			.build();
	}

	// 회원탈퇴 로직
	@Transactional
	public UserDeleteResponseDto deleteUser(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserException.UserNotFoundException::new);

		if (user.getIsDeleted()) {
			throw new UserException.DeletedUserAccessException();
		}

		user.markAsDeleted(userId); // 삭제 요청자의 ID를 전달

		return UserDeleteResponseDto.builder()
			.userId(user.getUserId()) // UUID 그대로 유지
			.build();
	}
}