package com.eureka.spartaonetoone.payment.application.dtos;

import com.eureka.spartaonetoone.payment.domain.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUpdateRequestDto {
    private String bank;
    private Integer price;
    private Payment.State state;
}
