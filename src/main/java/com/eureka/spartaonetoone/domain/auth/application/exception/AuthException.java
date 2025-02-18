package com.eureka.spartaonetoone.domain.auth.application.exception;
import com.eureka.spartaonetoone.common.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends CustomException {
    private AuthException(String errorCode, String message, HttpStatus status) {
        super(message, errorCode, status);
    }

    public static class DuplicateEmail extends AuthException {
        public DuplicateEmail() {
            super("AU-001", "이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST);
        }
    }
// 에러 수정하기
    public static class UserNotFound extends AuthException {
        public UserNotFound() {
            super("AU-002", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    public static class InvalidPassword extends AuthException {
        public InvalidPassword() {
            super("AU-003", "잘못된 비밀번호입니다.", HttpStatus.FORBIDDEN);
        }
    }

    public static class InvalidRoleException extends AuthException {
        public InvalidRoleException(String role) {
            super("AU-004", "유효하지 않은 역할: " + role, HttpStatus.FORBIDDEN);
        }
    }
}