package com.eureka.spartaonetoone.product.domain.repository;

import com.eureka.spartaonetoone.product.application.dtos.response.ProductGetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomProductRepository {
    public Page<ProductGetResponseDto> getProducts(Pageable pageable);

    public Page<ProductGetResponseDto> searchByCondition(UUID storeId, Integer minPrice, Integer maxPrice, String name, Pageable pageable);

}

