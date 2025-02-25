package com.eureka.spartaonetoone.product.application.exception;

import com.eureka.spartaonetoone.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ProductException extends CustomException {
    public ProductException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }

    public static class ProductNotFoundException extends ProductException {
        public ProductNotFoundException() {
            super("PR-001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    public static class ProductAlreadyExistsException extends ProductException {
        public ProductAlreadyExistsException() {
            super("PR-002", "해당 상점에 동일한 상품이 존재합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public static class ProductAccessDeniedException extends ProductException {
        public ProductAccessDeniedException() {
            super("PR-003", "해당 상품에 대한 수정, 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }
}
