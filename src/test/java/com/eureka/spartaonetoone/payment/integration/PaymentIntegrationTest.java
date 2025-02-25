package com.eureka.spartaonetoone.payment.integration;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.order.domain.OrderType;
import com.eureka.spartaonetoone.order.domain.repository.OrderRepository;
import com.eureka.spartaonetoone.payment.application.PaymentService;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentUpdateRequestDto;
import com.eureka.spartaonetoone.payment.application.exception.PaymentException;
import com.eureka.spartaonetoone.payment.domain.Payment;
import com.eureka.spartaonetoone.payment.domain.repository.PaymentRepository;
import com.eureka.spartaonetoone.store.domain.Store;
import com.eureka.spartaonetoone.store.domain.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static com.eureka.spartaonetoone.order.domain.Order.createOrder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@MockUser
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentIntegrationTest {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    OrderRepository orderRepository;
    private UUID savedPaymentId = null;


    @AfterAll
    void tearDown() {
        paymentRepository.deleteAll();
    }


    @Test
    @Order(1)
    @DisplayName("신규 결제 생성")
    void test1() {

        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Store store = Store.createStore(userDetails.getUserId(), "임시주문", StoreState.OPEN, "010-1111-1111",
                "임시설명", 20000, 50000, 3.0f, 5, List.of(UUID.randomUUID().toString()));

        UUID savedStoreId = storeRepository.save(store).getId();

        com.eureka.spartaonetoone.order.domain.Order order = createOrder(userDetails.getUserId(), store.getId(), OrderType.DELIVERY, "임시요청사항");

        UUID savedOrderId = orderRepository.save(order).getOrderId();

        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .bank("국민은행")
                .orderId(savedOrderId)
                .price(5000)
                .build();

        Payment payment = Payment.createPayment(request.getBank(), request.getOrderId(), request.getPrice());

        // when
        savedPaymentId = paymentService.savePayment(request);


        // then
        assertThat(savedPaymentId).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("결제 내역 조회")
    void test2() {

        // when
        Payment payment = paymentRepository.findById(savedPaymentId).orElseThrow(PaymentException.PaymentNotFoundException::new);

        // then
        assertThat(payment.getId()).isEqualTo(savedPaymentId);
    }

    @Test
    @Order(3)
    @DisplayName("결제 내역목록 조회")
    void test3() {

        for (int i = 0; i < 15; i++) {
            String bank = null;
            if (i % 2 == 0) {
                bank = "신한은행";
            } else {
                bank = "국민은행";
            }
            // given
            PaymentCreateRequestDto paymentCreateRequestDto = PaymentCreateRequestDto.builder()
                    .bank(bank)
                    .orderId(UUID.randomUUID())
                    .price(5000)
                    .build();
            paymentService.savePayment(paymentCreateRequestDto);
        }
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<PaymentGetResponseDto> payments = paymentService.getPayments(pageable);

        // then
        assertThat(payments).isNotNull();
        assertThat(payments.getTotalElements()).isEqualTo(10);
        assertThat(payments.getContent().stream().map(PaymentGetResponseDto::getPaymentState)
                .allMatch(paymentState -> Payment.State.SUCCESS.equals(paymentState)));
    }

    @Test
    @Order(4)
    @DisplayName("결제 내역 수정")
    @MockUser
    void test4() {

        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        PaymentUpdateRequestDto request = PaymentUpdateRequestDto.builder()
                .bank("하나은행")
                .state(Payment.State.FAILED)
                .price(10000)
                .build();

        // when
        paymentService.updatePayment(savedPaymentId, request, userDetails);
        Payment updatedPayment = paymentRepository.findById(savedPaymentId).get();

        // then
        assertThat(updatedPayment.getState()).isEqualTo(request.getState());
        assertThat(updatedPayment.getPrice()).isEqualTo(request.getPrice());
        assertThat(updatedPayment.getBank()).isEqualTo(request.getBank());
        assertThat(updatedPayment.getId()).isEqualTo(savedPaymentId);

    }

    @Test
    @Order(5)
    @DisplayName("결제 내역 삭제")
    @MockUser
    void test5() {

        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // when
        paymentService.deletePayment(savedPaymentId, userDetails);
        Payment softDeletedPayment = paymentRepository
                .findById(savedPaymentId)
                .orElseThrow(PaymentException.PaymentNotFoundException::new);

        // then
        assertThat(softDeletedPayment.getId()).isEqualTo(savedPaymentId);
        assertThat(softDeletedPayment.getIsDeleted()).isTrue();
        assertThat(softDeletedPayment.getDeletedBy()).isEqualTo(userDetails.getUserId());
        assertThat(softDeletedPayment.getDeletedAt()).isNotNull();
    }

}
