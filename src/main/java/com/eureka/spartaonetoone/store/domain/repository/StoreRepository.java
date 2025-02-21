package com.eureka.spartaonetoone.store.domain.repository;

import com.eureka.spartaonetoone.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

	List<Store> findByCategoryId(UUID categoryId);		// 특정 Category에 속한 Store 목록을 조회하는 메서드
}