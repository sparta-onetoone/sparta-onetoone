package com.eureka.spartaonetoone.domain.product.application.dtos;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductSearchDto {
    private String name;
    private String description;
    private Integer minPrice;
    private Integer maxPrice;
    private UUID storeId;

}
