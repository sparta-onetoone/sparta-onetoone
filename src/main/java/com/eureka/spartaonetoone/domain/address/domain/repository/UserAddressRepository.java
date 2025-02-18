package com.eureka.spartaonetoone.domain.address.domain.repository;

import com.eureka.spartaonetoone.domain.address.domain.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
    List<UserAddress> findByUser_UserId(UUID userId); // 특정 사용자 ID로 주소 조회
}
