package com.eureka.spartaonetoone.domain.payment.domain;

import com.eureka.spartaonetoone.common.utils.TimeStamp;
//import com.eureka.spartaonetoone.domain.payment.application.dtos.PaymentCreateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "p_payment")
public class Payment extends TimeStamp {

    @Id
    @UuidGenerator
    @NotNull
    @Column(name = "payment_id")
    private UUID id;
    @NotNull
    @Column(name = "order_id")
    private UUID orderId;
    @NotNull
    @Length(max = 100)
    private String bank;
    @Length(max = 100)
    private String state;
    @NotNull
    @Min(1000)
    private Integer price;
    @NotNull
    private Boolean isDeleted;

//    public static Payment from(PaymentCreateRequestDto paymentCreateRequestDto) {
//        return Payment.builder()
//                .bank(paymentCreateRequestDto.getBank())
//                .orderId(paymentCreateRequestDto.getOrderId())
//                .price(paymentCreateRequestDto.getPrice())
//                .isDeleted(paymentCreateRequestDto.getIsDeleted())
//                .build();
//    }

    public static Payment from(final String bank, final UUID orderId, Integer price, Boolean isDeleted) {
        return Payment.builder()
                .bank(bank)
                .orderId(orderId)
                .price(price)
                .isDeleted(isDeleted)
                .build();
    }

    public void deleteProduct() {
        this.isDeleted = true;
    }


}
