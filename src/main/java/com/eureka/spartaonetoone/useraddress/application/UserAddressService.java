package com.eureka.spartaonetoone.useraddress.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.user.application.exception.UserException;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressSearchRequestDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.response.UserAddressResponseDto;
import com.eureka.spartaonetoone.useraddress.application.exception.AddressException;
import com.eureka.spartaonetoone.useraddress.domain.UserAddress;
import com.eureka.spartaonetoone.useraddress.domain.repository.UserAddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAddressService {

	private final UserRepository userRepository;
	private final UserAddressRepository userAddressRepository;

	//주소 조회
	public Page<UserAddressResponseDto> getAddressesByUser(UUID userId, Pageable pageable) {
		Page<UserAddress> addresses = userAddressRepository.findByUser_UserId(userId, pageable);

		return addresses.map(UserAddressResponseDto::from); // 엔티티 -> DTO 변환
	}

	//주소 추가
	@Transactional
	public UserAddressResponseDto addAddress(UUID userId, UserAddressRequestDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserException.UserNotFoundException::new);  // 유저 없을 때 예외 발생

		UserAddress address = UserAddress.create(
			request.getCity(),
			request.getDistrict(),
			request.getRoadName(),
			request.getZipCode(),
			request.getDetail()
		);

		address.setUser(user);  // 유저와 주소 연결

		UserAddress savedAddress = userAddressRepository.save(address);  // DB에 저장

		if (savedAddress == null) {
			throw new RuntimeException("주소 추가 실패");
		}

		return UserAddressResponseDto.from(savedAddress);
	}

	// 주소 삭제 로직 (논리적 삭제)
	@Transactional
	public UserAddressResponseDto deleteAddress(UUID addressId) {
		UserAddress address = userAddressRepository.findById(addressId)
			.orElseThrow(AddressException.AddressNotFoundException::new);

		if (address.getIsDeleted()) {
			throw new AddressException.DeletedAddressAccessException();
		}

		address.deleteAddress(null); // 논리적 삭제 처리
		userAddressRepository.save(address);

		return UserAddressResponseDto.from(address);
	}

	// 주소 수정 로직
	@Transactional
	public UserAddressResponseDto updateAddress(UUID addressId, UserAddressRequestDto request) {
		// 주소 아이디로 기존 주소 조회
		UserAddress address = userAddressRepository.findById(addressId)
			.orElseThrow(AddressException.AddressNotFoundException::new);

		// 수정할 내용 적용
		address.updateAddress(
			request.getCity(),
			request.getDistrict(),
			request.getRoadName(),
			request.getZipCode(),
			request.getDetail()
		);

		// 수정된 주소 저장
		UserAddress updatedAddress = userAddressRepository.save(address);

		return UserAddressResponseDto.from(updatedAddress); // 수정된 주소 반환
	}

	// 사용자 주소 조회 (검색 조건 및 정렬 기능 추가)
	public Page<UserAddressResponseDto> searchUserAddresses(UUID userId, UserAddressSearchRequestDto request, Pageable pageable) {
		// UserAddressRepository에서 커스텀 메서드를 호출
		return userAddressRepository.searchUserAddresses(
			userId,
			request.getCity(),
			request.getDistrict(),
			request.getRoadName(),
			request.getZipCode(),
			pageable
		).map(UserAddressResponseDto::from);  // UserAddressResponseDto로 변환
	}
}