package com.eureka.spartaonetoone.product.application.dtos;

import com.eureka.spartaonetoone.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductGetResponseDto {
    private String name;
    private Integer price;
    private String description;

    public static ProductGetResponseDto from(final Product product) {
        return ProductGetResponseDto.builder()
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

}
