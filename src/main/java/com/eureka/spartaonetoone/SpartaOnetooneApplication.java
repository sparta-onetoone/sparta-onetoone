package com.eureka.spartaonetoone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication(scanBasePackages = "com.eureka.spartaonetoone")
public class SpartaOnetooneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpartaOnetooneApplication.class, args);
	}

}
