package com.eureka.spartaonetoone.common.utils;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserAuditorAware implements AuditorAware<UUID> {

	private final String ANONYMOUS_USER = "anonymousUser";

	@Override
	public Optional<UUID> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals(ANONYMOUS_USER)) {
			return Optional.empty();
		}

		if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
			return Optional.of(userDetails.getUserId());
		}

		return Optional.empty();
	}
}