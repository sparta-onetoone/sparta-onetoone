package com.eureka.spartaonetoone.review.application.exception;

import com.eureka.spartaonetoone.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ReviewException extends CustomException {
	public ReviewException(String errorCode, String message, HttpStatus status) {
		super(errorCode, message, status);
	}

	// 리뷰를 찾을 수 없을 때 발생하는 예외
	public static class ReviewNotFoundException extends ReviewException {
		public ReviewNotFoundException() {
			super("R-001", "해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
	}

	// 필요한 경우 추가 예외 (예: 중복 리뷰 등)를 정의할 수 있습니다.
}
