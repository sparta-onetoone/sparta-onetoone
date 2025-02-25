package com.eureka.spartaonetoone.order.application.exceptions;

import org.springframework.http.HttpStatus;

import com.eureka.spartaonetoone.common.exception.CustomException;

import lombok.Getter;

@Getter
public class OrderException extends CustomException {

	private OrderException(String message, String errorCode, HttpStatus httpStatus) {
		super(message, errorCode, httpStatus);
	}

	public static class NotFound extends OrderException {
		public NotFound() {
			super("주문을 찾을 수 없습니다.", "O-001", HttpStatus.NOT_FOUND);
		}
	}

	public static class EmptyCart extends OrderException {
		public EmptyCart() {
			super("장바구니가 비어있습니다.", "O-002", HttpStatus.BAD_REQUEST);
		}
	}

	public static class ProductClientError extends OrderException {
		public ProductClientError() {
			super("상품 서비스와 통신 중 오류가 발생했습니다.", "O-003", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static class ProductQuantityNotEnough extends OrderException {
		public ProductQuantityNotEnough() {
			super("상품의 재고가 부족합니다.", "O-004", HttpStatus.BAD_REQUEST);
		}
	}

	public static class PaymentError extends OrderException {
		public PaymentError() {
			super("결제에 실패했습니다.", "O-005", HttpStatus.BAD_REQUEST);
		}
	}

	public static class DeletePermissionDenied extends OrderException {
		public DeletePermissionDenied() {
			super("주문 삭제 권한이 없습니다.", "O-006", HttpStatus.FORBIDDEN);
		}
	}

	public static class CancelPermissionDenied extends OrderException {
		public CancelPermissionDenied() {
			super("주문 취소 권한이 없습니다.", "O-007", HttpStatus.FORBIDDEN);
		}
	}

	public static class CancelTimeLimit extends OrderException {
		public CancelTimeLimit() {
			super("주문 취소 가능 시간이 지났습니다.", "O-008", HttpStatus.BAD_REQUEST);
		}
	}

	public static class NotPendingStatus extends OrderException {
		public NotPendingStatus() {
			super("주문 상태가 처리 대기 중이 아닙니다.", "O-009", HttpStatus.BAD_REQUEST);
		}
	}

	public static class SearchPermissionDenied extends OrderException {
		public SearchPermissionDenied() {
			super("주문 조회 권한이 없습니다.", "O-010", HttpStatus.FORBIDDEN);
		}
	}
}
