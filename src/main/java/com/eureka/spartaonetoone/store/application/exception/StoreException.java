package com.eureka.spartaonetoone.store.application.exception;

import com.eureka.spartaonetoone.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class StoreException extends CustomException {
	public StoreException(String errorCode, String message, HttpStatus status) {
		super(errorCode, message, status);
	}

	// 가게를 찾을 수 없을 때 발생하는 예외
	public static class StoreNotFoundException extends StoreException {
		public StoreNotFoundException() {
			super("S-001", "해당 가게를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
	}

	// 가게가 이미 존재하는 경우 발생하는 예외 (필요한 경우)
	public static class StoreAlreadyExistsException extends StoreException {
		public StoreAlreadyExistsException() {
			super("S-002", "해당 가게가 이미 존재합니다.", HttpStatus.BAD_REQUEST);
		}
	}

	// 유효하지 않은 가게 상태 값이 전달되었을 때 발생하는 예외
	public static class InvalidStoreStateException extends StoreException {
		public InvalidStoreStateException() {
			super("S-003", "유효하지 않은 가게 상태입니다.", HttpStatus.BAD_REQUEST);
		}
	}
}
