package com.eureka.spartaonetoone.domain.payment.application.exception;

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
}
