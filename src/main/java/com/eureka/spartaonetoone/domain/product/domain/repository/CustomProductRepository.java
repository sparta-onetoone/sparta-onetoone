package com.eureka.spartaonetoone.domain.product.domain.repository;

import com.eureka.spartaonetoone.domain.product.application.dtos.ProductGetResponseDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {
    public Page<ProductGetResponseDto> getProducts(Pageable pageable);

    public Page<ProductGetResponseDto> searchByCondition(ProductSearchRequestDto productSearchRequestDto, Pageable pageable);

}

