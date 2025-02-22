package com.eureka.spartaonetoone.common.utils;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.eureka.spartaonetoone.user.domain.User;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

public class UserAuditorAware implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
			return Optional.of("SYSTEM");
		}

		if (authentication.getPrincipal() instanceof UserDetailsImpl) {
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			return Optional.of(userDetails.getUsername());
		}

		return Optional.of(authentication.getName());
	}
}