package com.eureka.spartaonetoone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class SpartaOnetooneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpartaOnetooneApplication.class, args);
	}

}
