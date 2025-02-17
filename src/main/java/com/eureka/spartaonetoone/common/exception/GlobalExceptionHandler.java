package com.eureka.spartaonetoone.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegrityViolationExceptionHandle(final DataIntegrityViolationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorMessage.of("잘못된 요청입니다.", "RQ-001"));
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
