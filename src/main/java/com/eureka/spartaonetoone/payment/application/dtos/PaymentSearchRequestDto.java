package com.eureka.spartaonetoone.payment.application.dtos;

import com.eureka.spartaonetoone.payment.domain.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentSearchRequestDto {
    private String bank;
    private Payment.State state;
    private Integer price;
}
