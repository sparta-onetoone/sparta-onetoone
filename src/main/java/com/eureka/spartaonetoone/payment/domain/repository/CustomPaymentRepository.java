package com.eureka.spartaonetoone.payment.domain.repository;

import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPaymentRepository {
    public Page<PaymentGetResponseDto> getPayments(Pageable pageable);

    public Page<PaymentGetResponseDto> searchPayments(String bank, Payment.State state, Integer price, Pageable pageable);
}
