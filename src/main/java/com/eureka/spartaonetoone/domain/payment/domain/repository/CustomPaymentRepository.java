package com.eureka.spartaonetoone.domain.payment.domain.repository;

import com.eureka.spartaonetoone.domain.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.domain.payment.application.dtos.PaymentSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPaymentRepository {
    public Page<PaymentGetResponseDto> getPayments(Pageable pageable);

    public Page<PaymentGetResponseDto> searchPayments(PaymentSearchDto paymentSearchDto, Pageable pageable);
}
