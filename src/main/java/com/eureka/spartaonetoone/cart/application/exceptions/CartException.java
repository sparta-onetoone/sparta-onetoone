package com.eureka.spartaonetoone.cart.application.exceptions;

import org.springframework.http.HttpStatus;

import com.eureka.spartaonetoone.common.exception.CustomException;

import lombok.Getter;

@Getter
public class CartException extends CustomException {

	private CartException(String message, String errorCode, HttpStatus httpStatus) {
		super(message, errorCode, httpStatus);
	}

	public static class NotFound extends CartException {
		public NotFound() {
			super("장바구니를 찾을 수 없습니다.", "C-001", HttpStatus.NOT_FOUND);
		}
	}

	public static class AlreadyExists extends CartException {
		public AlreadyExists() {
			super("이미 장바구니가 존재합니다.", "C-002", HttpStatus.BAD_REQUEST);
		}
	}
}
