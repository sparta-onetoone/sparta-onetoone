package com.eureka.spartaonetoone.product.application.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequestDto {
    @NotNull
    private UUID storeId;
    @NotNull
    @Length(max = 100, min = 1)
    private String name;
    @NotNull
    private String imageUrl;
    private String description;
    @NotNull
    @Min(value = 1000, message = "상품가격은 1000원 이상이어야 합니다.")
    private Integer price;
    @NotNull
    @Min(value = 1, message = "상품수량은 1개 이상이어야 합니다.")
    private Integer quantity;
    @NotNull
    private Boolean isDeleted;
}
