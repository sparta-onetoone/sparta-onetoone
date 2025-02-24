package com.eureka.spartaonetoone.product.application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReduceRequestDto {
    List<ReduceProductInfo> reduceProductInfoList = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReduceProductInfo {
        @JsonProperty("product_id")
        private UUID productId;
        private Integer quantity;

    }
}
