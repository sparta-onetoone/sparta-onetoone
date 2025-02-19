package com.eureka.spartaonetoone.domain.useraddress.application.exception;

import com.eureka.spartaonetoone.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AddressException extends CustomException {

    public AddressException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }

    public static class AddressNotFoundException extends AddressException {
        public AddressNotFoundException() {
            super("UA-001", "해당 주소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    public static class DeletedAddressAccessException extends AddressException {
        public DeletedAddressAccessException() {
            super("UA-002", "삭제된 주소에 접근할 수 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
