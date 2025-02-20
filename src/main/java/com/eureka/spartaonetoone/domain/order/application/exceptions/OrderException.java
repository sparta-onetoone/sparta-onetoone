package com.eureka.spartaonetoone.domain.order.application.exceptions;

import org.springframework.http.HttpStatus;

import com.eureka.spartaonetoone.common.exception.CustomException;

import lombok.Getter;

@Getter
public class OrderException extends CustomException {

	private OrderException(String message, String errorCode, HttpStatus httpStatus) {
		super(message, errorCode, httpStatus);
	}

	public static class EmptyCart extends OrderException {
		public EmptyCart() {
			super("장바구니가 비어있습니다.", "O-001", HttpStatus.BAD_REQUEST);
		}
	}

}
