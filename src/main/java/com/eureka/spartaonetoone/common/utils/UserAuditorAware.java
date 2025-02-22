package com.eureka.spartaonetoone.common.utils;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

@Service
public class UserAuditorAware implements AuditorAware<User> {
	@Override
	public Optional<User> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();


		if (principal instanceof UserDetailsImpl) {
			return Optional.of(((UserDetailsImpl)principal).getUser());
		} else {
			return Optional.empty();
		}
	}
}