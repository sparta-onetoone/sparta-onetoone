package com.eureka.spartaonetoone.product.application.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductSearchRequestDto {
    private String name;
    private String description;
    private Integer minPrice;
    private Integer maxPrice;
    @JsonProperty("store_id")
    private UUID storeId;

}
