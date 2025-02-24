package com.eureka.spartaonetoone.payment.application.dtos;

import com.eureka.spartaonetoone.payment.domain.Payment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class PaymentGetResponseDto {
    private UUID paymentId;
    private UUID orderId;
    private String paymentState;

    public static PaymentGetResponseDto from(Payment payment) {
        return PaymentGetResponseDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .paymentState(payment.getState().toString())
                .build();
    }


}
