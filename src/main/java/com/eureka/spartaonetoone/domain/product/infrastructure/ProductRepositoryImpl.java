package com.eureka.spartaonetoone.domain.product.infrastructure;

import com.eureka.spartaonetoone.domain.product.application.dtos.ProductGetResponseDto;
import com.eureka.spartaonetoone.domain.product.domain.Product;
import com.eureka.spartaonetoone.domain.product.domain.QProduct;
import com.eureka.spartaonetoone.domain.product.domain.repository.CustomProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Boolean existsByStoreIdAndProductName(UUID storeId, String productName) {
        return null;
    }

    @Override
    public Page<ProductGetResponseDto> getProducts(Pageable pageable) {
        List<Product> products = queryFactory.selectFrom(QProduct.product)
                .from(QProduct.product)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductGetResponseDto> responseDtos = products.stream().map(ProductGetResponseDto::from)
                .toList();

        return new PageImpl<>(responseDtos, pageable, products.size());
    }
}
