package com.eureka.spartaonetoone.domain.payment.application;

import com.eureka.spartaonetoone.domain.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.domain.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.domain.payment.application.dtos.PaymentSearchDto;
import com.eureka.spartaonetoone.domain.payment.application.exception.PaymentException;
import com.eureka.spartaonetoone.domain.payment.domain.Payment;
import com.eureka.spartaonetoone.domain.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public UUID savePayment(final PaymentCreateRequestDto paymentCreateRequestDto) {

        Payment payment = Payment.of(paymentCreateRequestDto.getBank(), paymentCreateRequestDto.getOrderId(),
                paymentCreateRequestDto.getPrice(), paymentCreateRequestDto.getIsDeleted());
        paymentRepository.save(payment);
        return payment.getId();
    }

    @Transactional(readOnly = true)
    public PaymentGetResponseDto getPayment(final UUID paymentId) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(PaymentException.PaymentNotFoundException::new);
        return PaymentGetResponseDto.from(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentGetResponseDto> getPayments(final Pageable pageable) {
        return paymentRepository.getPayments(pageable);
    }

    public UUID deletePayment(final UUID paymentId) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(PaymentException.PaymentNotFoundException::new);
        payment.deleteProduct();
        paymentRepository.save(payment);
        return payment.getId();
    }


    @Transactional(readOnly = true)
    public Page<PaymentGetResponseDto> searchPayments(final PaymentSearchDto paymentSearchDto, final Pageable pageable) {
        return paymentRepository.searchPayments(paymentSearchDto, pageable);
    }


}
