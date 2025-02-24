package com.eureka.spartaonetoone.category.application.exception;

public class CategoryException extends RuntimeException {

    private String errorCode;

    public CategoryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
