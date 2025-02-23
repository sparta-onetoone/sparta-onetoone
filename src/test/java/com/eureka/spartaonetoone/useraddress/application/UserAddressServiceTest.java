package com.eureka.spartaonetoone.useraddress.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.UserRole;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;
import com.eureka.spartaonetoone.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.useraddress.application.dtos.response.UserAddressResponseDto;
import com.eureka.spartaonetoone.useraddress.domain.UserAddress;
import com.eureka.spartaonetoone.useraddress.domain.repository.UserAddressRepository;

@ExtendWith(MockitoExtension.class)
class UserAddressServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserAddressRepository userAddressRepository;

	@InjectMocks
	private UserAddressService userAddressService;

	private UUID userId;
	private UUID addressId;
	private User user;
	private UserAddress address;
	private UserAddressRequestDto requestDto;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		addressId = UUID.randomUUID();

		// User 생성 시 UUID 사용
		user = User.create(userId.toString(), "testUser", "test@example.com", "testPassword", "010-1234-5678",
			UserRole.ADMIN);

		// address 객체 초기화
		address = UserAddress.builder()
			.addressId(addressId)
			.city("Seoul")
			.district("Gangnam")
			.roadName("Teheran-ro")
			.zipCode("12345")
			.detail("101-1")
			.isDeleted(false)
			.user(user)
			.build();

		requestDto = new UserAddressRequestDto(
			"Seoul",
			"Gangnam",
			"Teheran-ro",
			"12345",
			"101-1"
		);
	}

	@Test
	@DisplayName("주소를 정상적으로 추가할 수 있다.")
	void addAddress_success() {
		// given
		User user = mock(User.class); // User 객체를 모킹
		UUID userId = UUID.randomUUID();
		UserAddressRequestDto requestDto = new UserAddressRequestDto("city", "district", "road", "zip", "detail");

		// UserAddress 객체를 미리 생성
		UUID addressId = UUID.randomUUID(); // addressId 명시적으로 생성
		UserAddress address = UserAddress.builder()
			.addressId(addressId)
			.city("city")
			.district("district")
			.roadName("road")
			.zipCode("zip")
			.detail("detail")
			.isDeleted(false)
			.build(); // 주소 생성 시 addressId를 명시적으로 설정

		// 유저 객체와 연결된 주소 객체를 반환하도록 설정
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userAddressRepository.save(any(UserAddress.class))).thenReturn(address); // 주소가 저장되었을 때, 저장된 주소 객체 반환

		// when
		UserAddressResponseDto response = userAddressService.addAddress(userId, requestDto);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getCity()).isEqualTo(requestDto.getCity());
		assertThat(response.getDistrict()).isEqualTo(requestDto.getDistrict());
		verify(userAddressRepository).save(any(UserAddress.class));  // 주소가 저장되는지 확인
	}

	@Test
	@DisplayName("사용자 아이디로 주소 목록을 조회할 수 있다.")
	void getAddressesByUser_success() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<UserAddress> addressPage = new PageImpl<>(java.util.List.of(address));
		when(userAddressRepository.findByUser_UserId(userId, pageable)).thenReturn(addressPage);

		// when
		Page<UserAddressResponseDto> response = userAddressService.getAddressesByUser(userId, pageable);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getContent()).hasSize(1);  // 주소가 하나만 반환되는지 확인
		assertThat(response.getContent().get(0).getCity()).isEqualTo(address.getCity());
	}

	@Test
	@DisplayName("주소를 논리적으로 삭제할 수 있다.")
	void deleteAddress_success() {
		// given
		when(userAddressRepository.findById(addressId)).thenReturn(Optional.of(address));

		// when
		UserAddressResponseDto response = userAddressService.deleteAddress(addressId);

		// then
		assertThat(response).isNotNull();
		assertThat(address.getIsDeleted()).isTrue(); // 삭제된 상태 확인
		verify(userAddressRepository).save(address); // 삭제된 주소를 저장
	}

	@Test
	@DisplayName("주소를 수정할 수 있다.")
	void updateAddress_success() {
		// given
		UUID addressId = UUID.randomUUID();  // 임의의 addressId 생성 (테스트용)

		// User 객체를 mock으로 생성
		User user = mock(User.class);

		// 초기 주소 생성 (빌더를 통해 생성)
		UserAddress address = UserAddress.builder()
			.addressId(addressId) // 주소 ID 설정
			.city("old city")
			.district("old district")
			.roadName("old road")
			.zipCode("old zip")
			.detail("old detail")
			.isDeleted(false)
			.build();

		// 모킹된 User 객체를 address에 설정
		address.setUser(user);  // 'User' 객체는 mock으로 생성했기 때문에 실제 객체를 설정할 필요는 없음

		// addressId에 해당하는 주소를 반환하도록 설정
		when(userAddressRepository.findById(addressId)).thenReturn(Optional.of(address));
		when(userAddressRepository.save(any(UserAddress.class))).thenReturn(address); // 주소가 저장될 때, 저장된 주소 반환

		// 새로운 주소 정보
		UserAddressRequestDto requestDto = new UserAddressRequestDto("new city", "new district", "new road", "new zip",
			"new detail");

		// when
		UserAddressResponseDto response = userAddressService.updateAddress(addressId, requestDto);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getCity()).isEqualTo(requestDto.getCity());
		assertThat(response.getDistrict()).isEqualTo(requestDto.getDistrict());
		verify(userAddressRepository).save(any(UserAddress.class));  // 주소가 저장되는지 확인
	}

}