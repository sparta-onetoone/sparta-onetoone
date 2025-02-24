package com.eureka.spartaonetoone.common.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.eureka.spartaonetoone.common.utils.UserAuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "userAuditorAware")
public class JpaAuditingConfig {

	@Bean(name = "jpaAuditorAware")
	public AuditorAware<UUID> userAuditorAware() {
		return new UserAuditorAware();
	}
}
