package com.eureka.spartaonetoone.payment.presentation;

import com.eureka.spartaonetoone.payment.application.dtos.PaymentCreateRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentSearchRequestDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentUpdateRequestDto;
import com.eureka.spartaonetoone.user.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "결제 API", description = "결제 관련 API")
public interface PaymentApi {

    @Operation(summary = "결제 생성", description = "결제를 생성하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 생성 성공",
                    content = @Content(schema = @Schema(implementation = UUID.class)))
    })
    ResponseEntity<?> savePayment(PaymentCreateRequestDto request);

    @Operation(summary = "결제내역 단건 조회", description = "결제내역 1건을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제내역 조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentGetResponseDto.class)))
    })
    ResponseEntity<?> getPayment(UUID paymentId);

    @Operation(summary = "결제내역 목록 조회", description = "결제내역 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제내역 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<?> getPayments(int page, int limit);

    @Operation(summary = "결제내역 검색", description = "결제내역을 검색하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제내역 검색 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    ResponseEntity<?> searchPayments(PaymentSearchRequestDto request, int page, int limit);

    @Operation(summary = "결제내역 수정", description = "결제내역을 수정하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제내역 수정 성공",
                    content = @Content(schema = @Schema(implementation = UUID.class)))
    })
    ResponseEntity<?> updatePayment(UUID paymentId, PaymentUpdateRequestDto request, UserDetailsImpl userDetails);

    @Operation(summary = "결제내역 삭제", description = "결제내역을 삭제하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제내역 삭제 성공",
                    content = @Content(schema = @Schema(implementation = UUID.class)))
    })
    ResponseEntity<?> deletePayment(UUID paymentId, UserDetailsImpl userDetails);
}
