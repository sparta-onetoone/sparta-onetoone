package com.eureka.spartaonetoone.domain.product.domain.repository;

import com.eureka.spartaonetoone.domain.product.application.dtos.ProductGetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomProductRepository {
    public Boolean existsByStoreIdAndProductName(UUID storeId, String productName);

    public Page<ProductGetResponseDto> getProducts(Pageable pageable);
}
