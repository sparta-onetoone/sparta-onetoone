package com.eureka.spartaonetoone.common.utils;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponse<T> {

	private static final String SUCCESS_CODE = "S000";

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
}
