package com.eureka.spartaonetoone.common.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductRequest {

    @Getter
    @AllArgsConstructor
    public static class Reduce {
        List<ReduceProductInfo> reduceProductInfoList = new ArrayList<>();

        @Getter
        @AllArgsConstructor
        public static class ReduceProductInfo {
            @JsonProperty("product_id")
            private UUID productId;
            private Integer quantity;
        }
    }

}
