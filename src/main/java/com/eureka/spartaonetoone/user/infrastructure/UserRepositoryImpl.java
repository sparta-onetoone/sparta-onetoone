package com.eureka.spartaonetoone.user.infrastructure;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.eureka.spartaonetoone.user.application.dtos.request.UserSearchRequestDto;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.repository.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	// 회원 검색 메서드
	@Override
	public Page<User> searchByCondition(String username, String email, Pageable pageable) {
		String query = "SELECT u FROM User u WHERE 1=1";

		if (username != null && !username.isEmpty()) {
			query += " AND u.username LIKE :username";
		}

		if (email != null && !email.isEmpty()) {
			query += " AND u.email LIKE :email";
		}

		TypedQuery<User> typedQuery = entityManager.createQuery(query, User.class);

		if (username != null && !username.isEmpty()) {
			typedQuery.setParameter("username", "%" + username + "%");
		}
		if (email != null && !email.isEmpty()) {
			typedQuery.setParameter("email", "%" + email + "%");
		}

		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();

		typedQuery.setFirstResult(pageNumber * pageSize);
		typedQuery.setMaxResults(pageSize);

		List<User> userList = typedQuery.getResultList();

		String countQuery = "SELECT COUNT(u) FROM User u WHERE 1=1";
		if (username != null && !username.isEmpty()) {
			countQuery += " AND u.username LIKE :username";
		}
		if (email != null && !email.isEmpty()) {
			countQuery += " AND u.email LIKE :email";
		}

		TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery, Long.class);

		if (username != null && !username.isEmpty()) {
			countTypedQuery.setParameter("username", "%" + username + "%");
		}
		if (email != null && !email.isEmpty()) {
			countTypedQuery.setParameter("email", "%" + email + "%");
		}

		Long totalRecords = countTypedQuery.getSingleResult();

		return new PageImpl<>(userList, pageable, totalRecords);
	}

	// applySortAndPageSize 메서드 구현
	@Override
	public Pageable applySortAndPageSize(UserSearchRequestDto request, Pageable pageable) {
		// 페이지 크기 처리 (10, 30, 50만 허용, 나머지는 10으로 고정)
		int pageSize = request.getPageSize();
		if (pageSize != 10 && pageSize != 30 && pageSize != 50) {
			pageSize = 10; // 기본값으로 10으로 설정
		}

		// 정렬 기준 처리 (기본적으로 createdDate순, updatedDate순으로 정렬)
		Sort sort = Sort.by(Sort.Order.asc("createdDate")); // 기본값은 createdDate로 오름차순
		if ("updatedDate".equalsIgnoreCase(request.getSortBy())) {
			sort = Sort.by(Sort.Order.asc("updatedDate"));
		}

		// Pageable 객체 생성하여 반환
		return PageRequest.of(pageable.getPageNumber(), pageSize, sort);
	}
}
