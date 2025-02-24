package com.eureka.spartaonetoone.payment.presentation;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.order.domain.Order;
import com.eureka.spartaonetoone.order.domain.OrderType;
import com.eureka.spartaonetoone.order.domain.repository.OrderRepository;
import com.eureka.spartaonetoone.payment.application.PaymentService;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentUpdateRequestDto;
import com.eureka.spartaonetoone.payment.domain.Payment;
import com.eureka.spartaonetoone.store.domain.Store;
import com.eureka.spartaonetoone.store.domain.StoreState;
import com.eureka.spartaonetoone.store.domain.repository.StoreRepository;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static com.eureka.spartaonetoone.order.domain.Order.createOrder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PaymentControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StoreRepository storeRepository;


    private static List<PaymentCreateRequestDto> paymentCreateRequestDtos() {
        return List.of(
                PaymentCreateRequestDto.builder()
                        .price(0)
                        .bank(null)
                        .orderId(UUID.randomUUID())
                        .build(),
                PaymentCreateRequestDto.builder()
                        .price(500)
                        .bank("은행")
                        .orderId(UUID.randomUUID())
                        .build(),
                PaymentCreateRequestDto.builder()
                        .price(5000)
                        .bank("국민은행")
                        .orderId(null)
                        .build()
        );
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("결제 생성")
    @Test
    @MockUser
    void saveProduct() throws Exception {
        // given
        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .orderId(UUID.randomUUID())
                .bank("국민은행")
                .price(1500)
                .build();


        // when - then
        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("결제가 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.code").value("S000"));
    }

    @DisplayName("결제 생성 실패")
    @ParameterizedTest
    @MethodSource("paymentCreateRequestDtos")
    @MockUser
    void savePaymentFailed(PaymentCreateRequestDto request) throws Exception {
        // given -> @MethodSource로 주입받는다.


        // when - then
        mockMvc.perform(post("/api/v1/payments")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("결제 조회 성공")
    @Test
    @MockUser
    void getPayment() throws Exception {

        // given
        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .orderId(UUID.randomUUID())
                .bank("국민은행")
                .price(1500)
                .build();

        // when
        UUID savedPaymentId = paymentService.savePayment(request);
        PaymentGetResponseDto getResponseDto = paymentService.getPayment(savedPaymentId);

        // then
        mockMvc.perform(get("/api/v1/payments/{paymentId}", getResponseDto.getPaymentId())
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.message").value("결제내역이 성공적으로 조회되었습니다."));

        assertThat(savedPaymentId).isNotNull();
        assertThat(getResponseDto.getPaymentId()).isNotNull();
        assertThat(savedPaymentId).isEqualTo(getResponseDto.getPaymentId());

    }

    @DisplayName("결제 조회 실패")
    @Test
    @MockUser
    void getPaymentFailed() throws Exception {

        // given
        UUID nonExistentPaymentId = UUID.randomUUID();

        // when - then
        mockMvc.perform(get("/api/v1/payments/{paymentId}", nonExistentPaymentId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("결제 목록 전체 조회")
    @Test
    @MockUser
    void getPayments() throws Exception {

        // when - then
        mockMvc.perform(get("/api/v1/payments")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.message").value("결제내역목록이 성공적으로 조회되었습니다."));
    }

    @DisplayName("결제 내역 수정")
    @Test
    @MockUser
    void updatePayment() throws Exception {

        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Store store = Store.createStore(userDetails.getUserId(), "임시주문", StoreState.OPEN, "010-1111-1111",
                "임시설명", 20000, 50000, 3.0f, 5, UUID.randomUUID());

        UUID savedStoreId = storeRepository.save(store).getId();

        Order order = createOrder(userDetails.getUserId(), store.getId(), OrderType.DELIVERY, "임시요청사항");

        UUID savedOrderId = orderRepository.save(order).getOrderId();

        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .orderId(UUID.randomUUID())
                .bank("국민은행")
                .price(1500)
                .build();

        PaymentUpdateRequestDto updateRequest = PaymentUpdateRequestDto.builder()
                .price(50000)
                .state(Payment.State.FAILED)
                .bank("우리은행")
                .build();

        // when
        UUID savedPaymentId = paymentService.savePayment(request);

        // then
        mockMvc.perform(put("/api/v1/payments/{payment_id}", savedPaymentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.message").value("결제내역이 수정되었습니다."));

    }

    @DisplayName("결제 내역 삭제")
    @Test
    @MockUser
    void deletePayment() throws Exception {

        // given
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Store store = Store.createStore(userDetails.getUserId(), "임시주문", StoreState.OPEN, "010-1111-1111",
                "임시설명", 20000, 50000, 3.0f, 5, UUID.randomUUID());

        UUID savedStoreId = storeRepository.save(store).getId();

        com.eureka.spartaonetoone.order.domain.Order order = createOrder(userDetails.getUserId(), store.getId(), OrderType.DELIVERY, "임시요청사항");

        UUID savedOrderId = orderRepository.save(order).getOrderId();

        PaymentCreateRequestDto request = PaymentCreateRequestDto.builder()
                .orderId(UUID.randomUUID())
                .bank("국민은행")
                .price(1500)
                .build();

        // when
        UUID savedPaymentId = paymentService.savePayment(request);

        // then
        mockMvc.perform(delete("/api/v1/payments/{payment_id}", savedPaymentId)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.message").value("결제내역이 삭제되었습니다."));
    }

}