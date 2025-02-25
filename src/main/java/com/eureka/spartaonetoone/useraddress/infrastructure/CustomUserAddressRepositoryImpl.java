// package com.eureka.spartaonetoone.useraddress.infrastructure;
//
// import com.eureka.spartaonetoone.useraddress.domain.UserAddress;
// import com.eureka.spartaonetoone.useraddress.domain.repository.CustomUserAddressRepository;
// import com.querydsl.core.types.dsl.BooleanBuilder;
// import com.querydsl.jpa.impl.JPAQueryFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Repository;
//
// import java.util.List;
// import java.util.UUID;
//
// @Repository
// public class CustomUserAddressRepositoryImpl implements CustomUserAddressRepository {
//
// 	@Autowired
// 	private JPAQueryFactory queryFactory;
//
// 	// 동적 조건을 처리하는 메서드들
// 	private BooleanBuilder createCondition(UUID userId, String city, String district, String roadName, String zipCode) {
// 		BooleanBuilder builder = new BooleanBuilder();
//
// 		builder.and(QUserAddress.userAddress.isDeleted.eq(false)); // 기본 조건: 삭제되지 않은 주소만
//
// 		if (userId != null) {
// 			builder.and(QUserAddress.userAddress.user.userId.eq(userId)); // userId 조건 추가
// 		}
// 		if (city != null && !city.isEmpty()) {
// 			builder.and(QUserAddress.userAddress.city.contains(city)); // city 조건 추가
// 		}
// 		if (district != null && !district.isEmpty()) {
// 			builder.and(QUserAddress.userAddress.district.contains(district)); // district 조건 추가
// 		}
// 		if (roadName != null && !roadName.isEmpty()) {
// 			builder.and(QUserAddress.userAddress.roadName.contains(roadName)); // roadName 조건 추가
// 		}
// 		if (zipCode != null && !zipCode.isEmpty()) {
// 			builder.and(QUserAddress.userAddress.zipCode.contains(zipCode)); // zipCode 조건 추가
// 		}
//
// 		return builder;
// 	}
//
// 	@Override
// 	public Page<UserAddress> searchUserAddresses(UUID userId, String city, String district, String roadName, String zipCode, Pageable pageable) {
// 		// 조건을 빌드합니다.
// 		BooleanBuilder condition = createCondition(userId, city, district, roadName, zipCode);
//
// 		// 쿼리 실행
// 		List<UserAddress> addresses = queryFactory
// 			.selectFrom(QUserAddress.userAddress)
// 			.where(condition) // 조건 적용
// 			.offset(pageable.getOffset()) // 페이징 처리
// 			.limit(pageable.getPageSize()) // 페이징 처리
// 			.fetch();
//
// 		// 전체 데이터 수 계산
// 		long total = queryFactory
// 			.selectFrom(QUserAddress.userAddress)
// 			.where(condition) // 동일한 조건 적용
// 			.fetchCount();
//
// 		// 결과를 Page로 반환
// 		return new PageImpl<>(addresses, pageable, total);
// 	}
// }
