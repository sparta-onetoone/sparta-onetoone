package com.eureka.spartaonetoone.payment.application.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequestDto {
    @NotNull
    private UUID orderId;
    @NotNull
    @Length(min = 4, max = 100)
    private String bank;
    @NotNull
    @Min(value = 1000, message = "결제금액은 1000원 이상이어야 합니다.")
    private Integer price;

}
