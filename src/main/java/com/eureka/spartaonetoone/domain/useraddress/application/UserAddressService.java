package com.eureka.spartaonetoone.domain.useraddress.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eureka.spartaonetoone.domain.user.application.exception.UserException;
import com.eureka.spartaonetoone.domain.user.domain.User;
import com.eureka.spartaonetoone.domain.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.response.UserAddressResponseDto;
import com.eureka.spartaonetoone.domain.useraddress.application.exception.AddressException;
import com.eureka.spartaonetoone.domain.useraddress.domain.UserAddress;
import com.eureka.spartaonetoone.domain.useraddress.domain.repository.UserAddressRepository;

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

	//주소 추가 로직
	@Transactional
	public UserAddressResponseDto addAddress(UUID userId, UserAddressRequestDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserException.UserNotFoundException::new);

		UserAddress address = UserAddress.create(
			request.getCity(),
			request.getDistrict(),
			request.getRoadName(),
			request.getZipCode(),
			request.getDetail()
		);

		address.setUser(user);  // 유저와 주소 연결

		UserAddress savedAddress = userAddressRepository.save(address);
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
}