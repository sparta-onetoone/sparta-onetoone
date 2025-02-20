package com.eureka.spartaonetoone.product.application.dtos.response;

import com.eureka.spartaonetoone.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductGetResponseDto {
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Integer price;
    private UUID storeId;
    private String description;


    public static ProductGetResponseDto from(final Product product) {
        return ProductGetResponseDto.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .storeId(product.getStoreId())
                .build();
    }

}
