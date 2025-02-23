package com.eureka.spartaonetoone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.eureka.spartaonetoone")
public class SpartaOnetooneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpartaOnetooneApplication.class, args);
	}

}
