package com.eureka.spartaonetoone.payment.domain;

import com.eureka.spartaonetoone.common.utils.TimeStamp;
//import com.eureka.spartaonetoone.domain.payment.application.dtos.PaymentCreateRequestDto;
import jakarta.persistence.*;
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
    @NotNull
    @Enumerated(value = EnumType.STRING)
    private State state;
    @NotNull
    @Min(1000)
    private Integer price;
    @NotNull
    private Boolean isDeleted;


    public static Payment createPayment(final String bank, final UUID orderId, Integer price, Boolean isDeleted) {
        return Payment.builder()
                .bank(bank)
                .orderId(orderId)
                .price(price)
                .isDeleted(isDeleted)
                .state(State.SUCCESS)
                .build();
    }

    public void deleteProduct() {
        this.isDeleted = true;
    }

    @Getter
    public enum State {
        SUCCESS("성공"), FAILED("실패");

        private String value;

        State(String value) {
            this.value = value;
        }
    }


}
