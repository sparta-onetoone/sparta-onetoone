package com.eureka.spartaonetoone.common.utils;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserAuditorAware implements AuditorAware<UUID> {

	private final String ANONYMOUS_USER = "anonymousUser";

	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
			return Optional.of("SYSTEM");
		}

		if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
			return Optional.of(userDetails.getUserId());
		}

		return Optional.empty();
	}
}