package com.eureka.spartaonetoone.product.domain.repository;

import com.eureka.spartaonetoone.product.domain.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, CustomProductRepository {

    public Boolean existsByStoreIdAndName(@NotNull UUID storeId, @NotNull String name);
}
