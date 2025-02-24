package com.eureka.spartaonetoone.product.application.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDto {
    @NotNull
    @Length(max = 100, min = 1)
    private String name;
    private String description;
    @NotNull
    @Min(value = 1000, message = "상품가격은 1000원 이상이어야 합니다.")
    private Integer price;
    @NotNull
    @Min(value = 0, message = "상품수량은 0개 이상이어야 합니다.")
    private Integer quantity;
}
