package com.eureka.spartaonetoone.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.eureka.spartaonetoone.common.utils.UserAuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "userAuditorAware")
public class JpaAuditingConfig {

	@Bean
	public AuditorAware<String> userAuditorAware() {
		return new UserAuditorAware();
	}
}
