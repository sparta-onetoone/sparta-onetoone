package com.eureka.spartaonetoone.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Order(1)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityViolationExceptionHandle(final DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorMessage.of(ex.getMessage(), "DB-001"));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customExceptionHandle(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.of(ex.getMessage(), ex.getErrorCode()));
    }

    @AllArgsConstructor
    @Getter
    public static class ErrorMessage {
        String message;
        String errorCode;

        public static ErrorMessage of(String message, String errorCode) {
            return new ErrorMessage(message, errorCode);
        }
    }
}
