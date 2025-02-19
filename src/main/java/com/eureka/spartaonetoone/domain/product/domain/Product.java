package com.eureka.spartaonetoone.domain.product.domain;

import com.eureka.spartaonetoone.common.utils.TimeStamp;
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

    public static Product of(final UUID storeId, final String name
            , final String description, final Integer price, final Integer quantity, final Boolean isDeleted) {
        return Product.builder()
                .storeId(storeId)
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity)
                .isDeleted(isDeleted)
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

    public void updateProduct(final String name, final String description, Integer price, Integer quantity) {
        if (!this.quantity.equals(quantity)) {
            updateQuantity(quantity);
        }
        if (!this.price.equals(price)) {
            updatePrice(price);
        }
        if (!this.name.equals(name)) {
            updateName(name);
        }
        if (!this.description.equals(description)) {
            updateDescription(description);
        }
    }

    public void deleteProduct() {
        this.isDeleted = true;
    }

}
