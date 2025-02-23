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
        private String name;

        @JsonProperty("image_Url")
        private String imageUrl;

        private Integer quantity;

        private Integer price;

        @JsonProperty("store_id")
        private UUID storeId;
    }
}
