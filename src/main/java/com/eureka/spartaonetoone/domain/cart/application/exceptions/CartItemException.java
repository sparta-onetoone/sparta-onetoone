package com.eureka.spartaonetoone.domain.cart.application.exceptions;

import org.springframework.http.HttpStatus;

import com.eureka.spartaonetoone.common.exception.CustomException;

import lombok.Getter;

@Getter
public class CartItemException extends CustomException {

	private CartItemException(String message, String errorCode, HttpStatus httpStatus) {
		super(message, errorCode, httpStatus);
	}

	public static class MinQuantity extends CartItemException {
		public MinQuantity() {
			super("장바구니 상품의 최소 수량은 1개입니다.", "CI-001", HttpStatus.BAD_REQUEST);
		}
	}

	public static class NotFound extends CartItemException {
		public NotFound() {
			super("장바구니 상품을 찾을 수 없습니다.", "CI-002", HttpStatus.NOT_FOUND);
		}
	}
}
