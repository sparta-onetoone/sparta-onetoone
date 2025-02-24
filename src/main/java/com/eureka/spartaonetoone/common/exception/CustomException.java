package com.eureka.spartaonetoone.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

	private String errorCode;
	private String message;
	private HttpStatus status;
}
