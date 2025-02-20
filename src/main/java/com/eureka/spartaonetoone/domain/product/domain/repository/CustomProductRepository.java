package com.eureka.spartaonetoone.domain.product.domain.repository;

import com.eureka.spartaonetoone.domain.product.application.dtos.ProductGetResponseDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomProductRepository {
    public Page<ProductGetResponseDto> getProducts(Pageable pageable);

    public Page<ProductGetResponseDto> searchByCondition(ProductSearchDto productSearchDto, Pageable pageable);

}

