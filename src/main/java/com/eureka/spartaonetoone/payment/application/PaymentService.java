package com.eureka.spartaonetoone.payment.application;

import com.eureka.spartaonetoone.common.client.OrderClient;
import com.eureka.spartaonetoone.common.client.StoreClient;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentSearchRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentUpdateRequestDto;
import com.eureka.spartaonetoone.payment.application.exception.PaymentException;
import com.eureka.spartaonetoone.payment.domain.Payment;
import com.eureka.spartaonetoone.payment.domain.repository.PaymentRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
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
    private final OrderClient orderClient;
    private final StoreClient storeClient;

    public UUID savePayment(final PaymentCreateRequestDto paymentCreateRequestDto) {

        Payment payment = Payment.createPayment(paymentCreateRequestDto.getBank(), paymentCreateRequestDto.getOrderId(),
                paymentCreateRequestDto.getPrice());
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

    public UUID deletePayment(final UUID paymentId, final UserDetailsImpl userDetails) {
        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(PaymentException.PaymentNotFoundException::new);

        if (userDetails.getUser().getRole().toString().equals("ADMIN")) {
            payment.deletePayment(userDetails.getUserId());
            return payment.getId();
        }

        UUID storeId = orderClient.getStoreIdOfOrder(payment.getOrderId());
        UUID ownerId = storeClient.getOwnerId(storeId);

        if (userDetails.getUserId().equals(ownerId)) {
            payment.deletePayment(userDetails.getUserId());
            return payment.getId();
        }

        throw new PaymentException.PaymentAccessDeniedException();
    }

    public void updatePayment(final UUID paymentId,
                              final PaymentUpdateRequestDto request,
                              final UserDetailsImpl userDetails) {

        Payment payment = paymentRepository
                .findById(paymentId)
                .orElseThrow(PaymentException.PaymentNotFoundException::new);

        if (userDetails.getUser().getRole().toString().equals("ADMIN")) {
            payment.updatePayment(request.getPrice(), request.getBank(), request.getState());
            return;
        }

        UUID storeId = orderClient.getStoreIdOfOrder(payment.getOrderId());
        UUID ownerId = storeClient.getOwnerId(storeId);

        if (userDetails.getUserId().equals(ownerId)) {
            payment.updatePayment(request.getPrice(), request.getBank(), request.getState());
        }

        throw new PaymentException.PaymentAccessDeniedException();

    }


    @Transactional(readOnly = true)
    public Page<PaymentGetResponseDto> searchPayments(final PaymentSearchRequestDto request, final Pageable pageable) {
        return paymentRepository.searchPayments(request.getBank(), request.getState(), request.getPrice(), pageable);
    }


}
