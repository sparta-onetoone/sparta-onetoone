package com.eureka.spartaonetoone.domain.user.domain.repository;

import com.eureka.spartaonetoone.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email); // 이메일 중복 체크
    Optional<User> findByEmail(String email); // 로그인 이메일로 유저를 조회
    Optional<User> findByUsername(String username);
    Page<User> findAll(Pageable pageable);

}
