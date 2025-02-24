package com.eureka.spartaonetoone.cart.application.exceptions;

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

	public static class NotFoundInCart extends CartItemException {
		public NotFoundInCart() {
			super("장바구니에 해당 상품이 존재하지 않습니다.", "CI-003", HttpStatus.NOT_FOUND);
		}
	}

	// 상품 재고 수량 부족
	public static class NotEnoughStock extends CartItemException {
		public NotEnoughStock() {
			super("상품의 재고 수량이 부족합니다.", "CI-004", HttpStatus.BAD_REQUEST);
		}
	}

	public static class ProductClientError extends CartItemException {
		public ProductClientError() {
			super("상품 서비스 호출 중 오류가 발생했습니다.", "CI-005", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
