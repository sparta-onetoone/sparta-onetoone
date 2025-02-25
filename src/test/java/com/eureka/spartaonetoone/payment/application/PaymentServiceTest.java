package com.eureka.spartaonetoone.payment.application;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.order.application.OrderService;
import com.eureka.spartaonetoone.order.domain.Order;
import com.eureka.spartaonetoone.order.domain.OrderType;
import com.eureka.spartaonetoone.order.domain.repository.OrderRepository;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentUpdateRequestDto;
import com.eureka.spartaonetoone.payment.domain.Payment;
import com.eureka.spartaonetoone.payment.domain.repository.PaymentRepository;
import com.eureka.spartaonetoone.store.application.StoreService;
import com.eureka.spartaonetoone.store.domain.Store;
import com.eureka.spartaonetoone.store.domain.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;
import java.util.UUID;

import static com.eureka.spartaonetoone.order.domain.Order.createOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    StoreService storeService;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("결제 생성 성공")
    @Test
    @MockUser
    void savePayment() {
        //given
        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .bank("국민은행")
                .price(10000)
                .orderId(UUID.randomUUID())
                .build();

        //when
        UUID savedPaymentId = paymentService.savePayment(request);


        //then
        assertThat(savedPaymentId).isNotNull();
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

        Store store = Store.createStore(userDetails.getUserId(), "임시주문", StoreState.OPEN, "010-1111-1111",
                "임시설명", 20000, 50000, 3.0f, 5, List.of(UUID.randomUUID().toString()));

        UUID savedStoreId = storeRepository.save(store).getId();

        Order order = createOrder(userDetails.getUserId(), store.getId(), OrderType.DELIVERY, "임시요청사항");

        UUID savedOrderId = orderRepository.save(order).getOrderId();

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
        paymentService.updatePayment(savedPaymentId, updateRequest, userDetails);
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

        Store store = Store.createStore(userDetails.getUserId(), "임시주문", StoreState.OPEN, "010-1111-1111",
                "임시설명", 20000, 50000, 3.0f, 5, List.of(UUID.randomUUID().toString()));

        UUID savedStoreId = storeRepository.save(store).getId();

        com.eureka.spartaonetoone.order.domain.Order order = createOrder(userDetails.getUserId(), store.getId(), OrderType.DELIVERY, "임시요청사항");

        UUID savedOrderId = orderRepository.save(order).getOrderId();

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