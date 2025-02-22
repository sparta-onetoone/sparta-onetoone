package com.eureka.spartaonetoone.common.utils;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

@Service
public class UserAuditorAware implements AuditorAware<String> {  // 반환 타입을 String으로 변경
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof UserDetailsImpl) {
			User user = ((UserDetailsImpl) principal).getUser();
			return Optional.of(user.getUsername());  // username을 String으로 반환
		} else {
			return Optional.empty();
		}
	}
}