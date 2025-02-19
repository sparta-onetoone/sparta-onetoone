package com.eureka.spartaonetoone.domain.product.infrastructure;

import com.eureka.spartaonetoone.domain.product.application.dtos.ProductGetResponseDto;
import com.eureka.spartaonetoone.domain.product.application.dtos.ProductSearchDto;
import com.eureka.spartaonetoone.domain.product.domain.Product;
import com.eureka.spartaonetoone.domain.product.domain.QProduct;
import com.eureka.spartaonetoone.domain.product.domain.repository.CustomProductRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<ProductGetResponseDto> getProducts(Pageable pageable) {
        List<Product> products = queryFactory.selectFrom(QProduct.product)
                .from(QProduct.product)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductGetResponseDto> responseDtos = products
                .stream()
                .map(ProductGetResponseDto::from)
                .toList();

        return new PageImpl<>(responseDtos, pageable, products.size());
    }

    @Override
    public Page<ProductGetResponseDto> searchByCondition(ProductSearchDto productSearchDto, Pageable pageable) {

        List<Product> products = queryFactory
                .selectFrom(QProduct.product)
                .from(QProduct.product)
                .where(searchByMaxPrice(productSearchDto.getMaxPrice()), searchByMinPrice(productSearchDto.getMinPrice()),
                        searchByName(productSearchDto.getName()), searchByStore(productSearchDto.getStoreId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        List<ProductGetResponseDto> responseDtos = products
                .stream()
                .map(ProductGetResponseDto::from)
                .toList();

        return new PageImpl<>(responseDtos, pageable, products.size());

    }


    BooleanExpression searchByMaxPrice(Integer maxPrice) {
        return maxPrice != null ? QProduct.product.price.loe(maxPrice) : null;
    }

    BooleanExpression searchByMinPrice(Integer minPrice) {
        return minPrice != null ? QProduct.product.price.goe(minPrice) : null;
    }

    BooleanExpression searchByName(final String name) {
        return StringUtils.hasText(name) ? QProduct.product.name.contains(name) : null;
    }

    BooleanExpression searchByStore(final UUID storeId) {
        return storeId != null ? QProduct.product.storeId.eq(storeId) : null;
    }
}
