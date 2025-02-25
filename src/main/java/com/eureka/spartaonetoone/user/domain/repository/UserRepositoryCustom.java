package com.eureka.spartaonetoone.user.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eureka.spartaonetoone.user.application.dtos.request.UserSearchRequestDto;
import com.eureka.spartaonetoone.user.domain.User;

public interface UserRepositoryCustom {
	Page<User> searchByCondition(String username, String email, Pageable pageable);
	Pageable applySortAndPageSize(UserSearchRequestDto request, Pageable pageable);

}
