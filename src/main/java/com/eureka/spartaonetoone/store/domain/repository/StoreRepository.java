package com.eureka.spartaonetoone.store.domain.repository;

import com.eureka.spartaonetoone.store.domain.Store;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID>, QuerydslPredicateExecutor<Store> {

	// 소프트 삭제되지 않은 활성 상점을 검색
	@Query("SELECT s FROM Store s WHERE s.isDeleted = false")
	List<Store> findAllActiveStores();

	// 카테고리 ID로 상점을 검색하며 소프트 삭제된 상점은 제외
//	@Query("SELECT s FROM Store s JOIN s.categoryIds c WHERE c.id = :categoryId AND s.isDeleted = false")
//	List<Store> findByCategoryIdAndActive(@Param("categoryIds") UUID categoryId);

	// 보다 복잡한 쿼리 요구사항에 대응하기 위한 사양(Specification) 사용 (선택적)
	List<Store> findAll(Specification<Store> spec);
}
