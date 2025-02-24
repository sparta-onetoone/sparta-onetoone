package com.eureka.spartaonetoone.payment.application;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentUpdateRequestDto;
import com.eureka.spartaonetoone.payment.domain.Payment;
import com.eureka.spartaonetoone.payment.domain.repository.PaymentRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionSystemException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;


    @DisplayName("결제 생성 성공")
    @Test
    void savePayment() {

        //given
        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .bank("국민은행")
                .price(10000)
                .orderId(UUID.randomUUID())
                .build();

        //when
        UUID savePaymentId = paymentService.savePayment(request);


        //then
        assertThat(savePaymentId).isNotNull();
    }

    @DisplayName("결제 생성 실패")
    @Test
    void savePaymentFailed() {

        //given -> 은행에 대한 정보를 누락
        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .price(10000)
                .orderId(UUID.randomUUID())
                .build();

        //when-then
        assertThatThrownBy(() -> paymentService.savePayment(request))
                .isInstanceOf(TransactionSystemException.class);
    }

    @DisplayName("결제 조회 성공")
    @Test
    void getPayment() {

        //given
        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .bank("국민은행")
                .price(10000)
                .orderId(UUID.randomUUID())
                .build();

        //when
        UUID savePaymentId = paymentService.savePayment(request);
        Payment payment = paymentRepository.findById(savePaymentId).get();

        //then
        assertThat(payment.getId()).isEqualTo(savePaymentId);

    }

    @DisplayName("결제 수정 성공")
    @Test
    @MockUser
    void updatePayment() {

        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        PaymentCreateRequestDto createRequest = PaymentCreateRequestDto.builder()
                .price(20000)
                .orderId(UUID.randomUUID())
                .bank("국민은행")
                .build();

        PaymentUpdateRequestDto updateRequest = PaymentUpdateRequestDto.builder()
                .price(50000)
                .state(Payment.State.FAILED)
                .bank("우리은행")
                .build();

        //when
        UUID savedPaymentId = paymentService.savePayment(createRequest);
        paymentService.updatePayment(savedPaymentId, updateRequest, userDetails.getUserId());
        Payment updatedPayment = paymentRepository.findById(savedPaymentId).get();

        //then
        assertThat(updatedPayment.getState()).isEqualTo(Payment.State.FAILED);
        assertThat(updatedPayment.getId()).isEqualTo(savedPaymentId);
    }


    @DisplayName("결제 삭제 성공")
    @Test
    @MockUser
    void deletePayment() {

        //given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .bank("국민은행")
                .price(10000)
                .orderId(UUID.randomUUID())
                .build();

        //when
        UUID savePaymentId = paymentService.savePayment(request);
        Payment payment = paymentRepository.findById(savePaymentId).get();
        payment.deletePayment(userDetails.getUserId());
        paymentRepository.saveAndFlush(payment);

        //then
        assertThat(paymentRepository.findById(savePaymentId).get().getIsDeleted()).isTrue();
    }


}