package com.eureka.spartaonetoone.common.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentRequest {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private final String bank = "국민은행";
        private UUID orderId;
        private Integer price;
        private Boolean isDeleted = false;
        
        public Create(UUID orderId, Integer price) {
            this.orderId = orderId;
            this.price = price;
        }
    }
}
