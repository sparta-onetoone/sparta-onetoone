package com.eureka.spartaonetoone.useraddress.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eureka.spartaonetoone.useraddress.domain.UserAddress;

public interface CustomUserAddressRepository {
	Page<UserAddress> searchUserAddresses(UUID userId, String city, String district, String roadName, String zipCode, Pageable pageable);

}
