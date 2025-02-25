package com.eureka.spartaonetoone.payment.domain;

import com.eureka.spartaonetoone.common.utils.TimeStamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
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


    public static Payment createPayment(final String bank, final UUID orderId, Integer price) {
        return Payment.builder()
                .bank(bank)
                .orderId(orderId)
                .price(price)
                .isDeleted(Boolean.FALSE)
                .state(State.SUCCESS)
                .build();
    }

    public void deletePayment(UUID deletedBy) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

    public void updateBank(String bank) {
        this.bank = bank;
    }

    public void updateState(Payment.State state) {
        this.state = state;
    }

    public void updatePayment(Integer price, String bank, Payment.State state) {
        if (price != null) {
            updatePrice(price);
        }
        if (bank != null) {
            updateBank(bank);
        }
        if (state != null) {
            updateState(state);
        }
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
