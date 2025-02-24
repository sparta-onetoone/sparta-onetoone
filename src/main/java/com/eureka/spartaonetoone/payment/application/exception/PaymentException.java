package com.eureka.spartaonetoone.payment.application.exception;

import com.eureka.spartaonetoone.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PaymentException extends CustomException {
    public PaymentException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }

    public static class PaymentNotFoundException extends PaymentException {
        public PaymentNotFoundException() {
            super("PM-001", "해당 결제내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    public static class PaymentAccessDeniedException extends PaymentException {
        public PaymentAccessDeniedException() {
            super("PM-002", "해당 결제내역에 접근권한이 존재하지 않습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
