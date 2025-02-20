package com.eureka.spartaonetoone.payment.presentation;

import com.eureka.spartaonetoone.common.client.ProductClient;
import com.eureka.spartaonetoone.common.dtos.ProductResponse;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.payment.application.PaymentService;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentSearchRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@RestController
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> savePayment(@RequestBody
                                         @Valid final PaymentCreateRequestDto paymentCreateRequestDto) {
        UUID savedPaymentId = paymentService.savePayment(paymentCreateRequestDto);
        return ResponseEntity.ok(CommonResponse.success(savedPaymentId, "결제가 성공적으로 완료되었습니다."));
    }

    @GetMapping("{payment_id}")
    public ResponseEntity<?> getPayment(@PathVariable(name = "payment_id") final UUID payment_id) {
        PaymentGetResponseDto getResponseDto = paymentService.getPayment(payment_id);
        return ResponseEntity.ok(CommonResponse.success(getResponseDto, "결제내역이 성공적으로 조회되었습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getPayments(@RequestParam(name = "page", defaultValue = "1") final int page,
                                         @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        Page<PaymentGetResponseDto> payments = paymentService.getPayments(pageable);
        return ResponseEntity.ok(CommonResponse.success(payments, "결제내역목록이 성공적으로 조회되었습니다."));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPayments(final PaymentSearchRequestDto request,
                                            @RequestParam(name = "page", defaultValue = "1") final int page,
                                            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        Page<PaymentGetResponseDto> getResponseDtos = paymentService.searchPayments(request, pageable);
        return ResponseEntity.ok(CommonResponse.success(getResponseDtos, "결제내역목록이 성공적으로 조회되었습니다."));
    }

    private Pageable getPageable(int page, int pageSize) {
        return PageRequest.of(page - 1, pageSize);
    }


    @DeleteMapping("{payment_id}")
    public ResponseEntity<?> deletePayment(@PathVariable(name = "payment_id") final UUID payment_id) {
        UUID deletePaymentId = paymentService.deletePayment(payment_id);
        return ResponseEntity.ok(CommonResponse.success(deletePaymentId, "결제내역이 삭제되었습니다."));
    }


}
