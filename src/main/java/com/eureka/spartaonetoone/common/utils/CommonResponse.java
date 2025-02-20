package com.eureka.spartaonetoone.common.utils;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponse<T> {

    private static final String SUCCESS_CODE = "S000";
    private static final String FAIL_CODE = "F000";

    private String code;
    private T data;
    private String message;

    public static <T> CommonResponse<T> success(T data, String message) {
        return CommonResponse.<T>builder()
                .code(SUCCESS_CODE)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> CommonResponse<T> fail() {
        return CommonResponse.<T>builder()
                .code(FAIL_CODE)
                .data(null)
                .message(null)
                .build();
    }
}
