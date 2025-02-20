package com.eureka.spartaonetoone.product.domain;

import com.eureka.spartaonetoone.common.utils.TimeStamp;
import com.eureka.spartaonetoone.product.application.dtos.ProductCreateRequestDto;
import com.eureka.spartaonetoone.product.application.dtos.ProductUpdateRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "p_product",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "store_product",
                        columnNames = {"store_id", "name"}
                )
        })
public class Product extends TimeStamp {
    @Id
    @UuidGenerator
    @NotNull
    @Column(name = "product_id")
    private UUID id;
    @NotNull
    @Column(name = "store_id")
    private UUID storeId;
    @NotNull
    @Min(value = 1000, message = "상품가격은 1000원 이상이어야 합니다.")
    private Integer price;
    @NotNull
    @Length(max = 100, min = 1)
    private String name;
    @Column(nullable = true)
    @Length(max = 100, min = 1)
    private String description;
    @NotNull
    @Min(value = 0, message = "상품수량은 0개 이상이어야 합니다.")
    private Integer quantity;
    @NotNull
    private Boolean isDeleted;

    public static Product from(final ProductCreateRequestDto productCreateRequestDto) {
        return Product.builder()
                .storeId(productCreateRequestDto.getStoreId())
                .name(productCreateRequestDto.getName())
                .description(productCreateRequestDto.getDescription())
                .price(productCreateRequestDto.getPrice())
                .quantity(productCreateRequestDto.getQuantity())
                .isDeleted(productCreateRequestDto.getIsDeleted())
                .build();
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateProduct(ProductUpdateRequestDto productUpdateRequestDto) {
        if (!this.quantity.equals(productUpdateRequestDto.getQuantity())) {
            updateQuantity(productUpdateRequestDto.getQuantity());
        }
        if (!this.price.equals(productUpdateRequestDto.getPrice())) {
            updatePrice(productUpdateRequestDto.getPrice());
        }
        if (!this.name.equals(productUpdateRequestDto.getName())) {
            updateName(productUpdateRequestDto.getName());
        }
        if (!this.description.equals(productUpdateRequestDto.getDescription())) {
            updateDescription(productUpdateRequestDto.getDescription());
        }
    }

    public void deleteProduct() {
        this.isDeleted = true;
    }

}
