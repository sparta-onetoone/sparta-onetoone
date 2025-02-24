package com.eureka.spartaonetoone.user.application.exception;

import com.eureka.spartaonetoone.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UserException extends CustomException {

    public UserException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }

    public static class UserNotFoundException extends UserException {
        public UserNotFoundException() {
            super("U-001", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    public static class DeletedUserAccessException extends UserException {
        public DeletedUserAccessException() {
            super("U-002", "삭제된 사용자에 접근할 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

}