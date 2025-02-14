package com.eureka.spartaonetoone.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;

@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> customExceptionHandle(CustomException ex) {
		return ResponseEntity
			.status(ex.getStatus())
			.body(ErrorMessage.of(ex.getMessage(), ex.getErrorCode()));
	}

	@AllArgsConstructor
	public static class ErrorMessage {
		String message;
		String errorCode;

		public static ErrorMessage of(String message, String errorCode) {
			return new ErrorMessage(message, errorCode);
		}
	}
}
