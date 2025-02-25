package com.eureka.spartaonetoone.useraddress.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eureka.spartaonetoone.useraddress.domain.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID>, CustomUserAddressRepository {
	Page<UserAddress> findByUser_UserId(UUID userId, Pageable pageable);
}
