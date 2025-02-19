package com.eureka.spartaonetoone.domain.useraddress.application;

import com.eureka.spartaonetoone.domain.useraddress.application.dtos.request.UserAddressRequestDto;
import com.eureka.spartaonetoone.domain.useraddress.application.dtos.response.UserAddressResponseDto;
import com.eureka.spartaonetoone.domain.useraddress.application.exception.AddressException;
import com.eureka.spartaonetoone.domain.useraddress.domain.UserAddress;
import com.eureka.spartaonetoone.domain.useraddress.domain.repository.UserAddressRepository;
import com.eureka.spartaonetoone.domain.user.application.exception.UserException;
import com.eureka.spartaonetoone.domain.user.domain.User;
import com.eureka.spartaonetoone.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAddressService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    // 특정 사용자 주소 조회
    public List<UserAddressResponseDto> getAddressesByUser(UUID userId) {
        List<UserAddress> addresses = userAddressRepository.findByUser_UserId(userId);
        return addresses.stream().map(UserAddressResponseDto::from) // 엔티티 -> DTO 변환
                .collect(Collectors.toList());
    }

    // 주소 추가 로직
    @Transactional
    public UserAddressResponseDto addAddress(UUID userId, UserAddressRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(UserException.UserNotFoundException::new);

        UserAddress address = UserAddress.builder().city(request.getCity()).district(request.getDistrict()).roadName(request.getRoadName()).zipCode(request.getZipCode()).detail(request.getDetail()).isDeleted(false).user(user).build();

        UserAddress savedAddress = userAddressRepository.save(address);
        return UserAddressResponseDto.from(savedAddress); // 엔티티 -> DTO 변환 후 반환
    }

    // 주소 삭제 로직 (논리적 삭제)
    @Transactional
    public UserAddressResponseDto deleteAddress(UUID addressId) {
        UserAddress address = userAddressRepository.findById(addressId).orElseThrow(AddressException.AddressNotFoundException::new);

        if (address.getIsDeleted()) {
            throw new AddressException.DeletedAddressAccessException();
        }

        address.deleteAddress(null); // 논리적 삭제 처리
        userAddressRepository.save(address);

        return UserAddressResponseDto.from(address); // 엔티티 -> DTO 변환 후 반환
    }
}