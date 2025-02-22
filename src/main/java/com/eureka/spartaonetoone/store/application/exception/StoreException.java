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
			// 오류 코드 "S-001"을 메시지에 포함시켜 반환합니다.
			super("S-001", "S-001: 해당 가게를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
	}

	// 필요시 다른 예외들도 여기에 정의
	public static class StoreAlreadyExistsException extends StoreException {
		public StoreAlreadyExistsException() {
			super("S-002", "S-002: 해당 가게가 이미 존재합니다.", HttpStatus.BAD_REQUEST);
		}
	}

	public static class InvalidStoreStateException extends StoreException {
		public InvalidStoreStateException() {
			super("S-003", "S-003: 유효하지 않은 가게 상태입니다.", HttpStatus.BAD_REQUEST);
		}
	}


	// 수정 권한이 없을 때 발생하는 예외
	public static class NoPermissionToUpdateException extends StoreException {
		public NoPermissionToUpdateException() {
			super("S-004", "S-004: 수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
	}

	// 삭제 권한이 없을 때 발생하는 예외
	public static class NoPermissionToDeleteException extends StoreException {
		public NoPermissionToDeleteException() {
			super("S-005", "S-005: 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
	}
}
