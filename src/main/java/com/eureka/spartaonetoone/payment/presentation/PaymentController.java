package com.eureka.spartaonetoone.payment.presentation;

import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.eureka.spartaonetoone.payment.application.PaymentService;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentSearchDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(CommonResponse.success(paymentService.savePayment(paymentCreateRequestDto), "결제가 성공적으로 완료되었습니다."));
    }

    @GetMapping("{payment_id}")
    public ResponseEntity<?> getPayment(@PathVariable(name = "payment_id") final UUID payment_id) {
        return ResponseEntity.ok(CommonResponse.success(paymentService.getPayment(payment_id), "결제내역이 성공적으로 조회되었습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getPayments(@RequestParam(name = "page", defaultValue = "1") final int page,
                                         @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        return ResponseEntity.ok(CommonResponse.success(paymentService.getPayments(pageable), "결제내역목록이 성공적으로 조회되었습니다."));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPayments(final PaymentSearchDto paymentSearchDto,
                                            @RequestParam(name = "page", defaultValue = "1") final int page,
                                            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {

        Pageable pageable = getPageable(page, pageSize);
        return ResponseEntity.ok(paymentService.searchPayments(paymentSearchDto, pageable));
    }

    private Pageable getPageable(int page, int pageSize) {
        return PageRequest.of(page - 1, pageSize);
    }


    @DeleteMapping("{payment_id}")
    public ResponseEntity<?> deletePayment(@PathVariable(name = "payment_id") final UUID payment_id) {
        return ResponseEntity.ok(CommonResponse.success(paymentService.deletePayment(payment_id), "결제내역이 삭제되었습니다."));
    }


}
