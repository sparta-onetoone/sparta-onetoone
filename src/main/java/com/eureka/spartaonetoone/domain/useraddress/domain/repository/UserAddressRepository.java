package com.eureka.spartaonetoone.domain.useraddress.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eureka.spartaonetoone.domain.useraddress.domain.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
	List<UserAddress> findByUser_UserId(UUID userId); // 특정 사용자 ID로 주소 조회

	Page<UserAddress> findByUser_UserId(UUID userId, Pageable pageable);
}
