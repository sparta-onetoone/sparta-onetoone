package com.eureka.spartaonetoone.common.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class ProductResponse {

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Get {
        @JsonProperty("name")
        private String name;

        @JsonProperty("imageUrl")
        private String imageUrl;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("price")
        private Integer price;

        @JsonProperty("storeId")
        private UUID storeId;
    }
}
