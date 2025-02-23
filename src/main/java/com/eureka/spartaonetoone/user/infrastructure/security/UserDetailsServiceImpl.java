package com.eureka.spartaonetoone.user.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eureka.spartaonetoone.user.application.exception.UserException;
import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.domain.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// username을 통해 User를 DB에서 조회
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UserException.UserNotFoundException()); // 사용자 찾을 수 없을 때 예외 던짐

		// 삭제된 사용자라면 DeletedUserAccessException을 던짐
		if (user.getIsDeleted()) {
			throw new UserException.DeletedUserAccessException(); // 삭제된 사용자는 접근 불가
		}

		// User 객체를 UserDetailsImpl에 전달
		return new UserDetailsImpl(user); // User 객체를 전달하여 UserDetailsImpl 생성
	}
}