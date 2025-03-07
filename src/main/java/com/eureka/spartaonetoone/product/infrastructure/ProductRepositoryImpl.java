package com.eureka.spartaonetoone.product.infrastructure;

import com.eureka.spartaonetoone.product.application.dtos.response.ProductGetResponseDto;
import com.eureka.spartaonetoone.product.domain.Product;
import com.eureka.spartaonetoone.product.domain.repository.CustomProductRepository;
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

import static com.eureka.spartaonetoone.product.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<ProductGetResponseDto> getProducts(Pageable pageable) {
        List<Product> products = queryFactory.selectFrom(product)
                .from(product)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.asc(), product.updatedAt.asc())
                .fetch();

        List<ProductGetResponseDto> responseDtos = products
                .stream()
                .map(ProductGetResponseDto::from)
                .toList();

        return new PageImpl<>(responseDtos, pageable, products.size());
    }

    @Override
    public Page<ProductGetResponseDto> searchByCondition(UUID storeId, Integer minPrice, Integer maxPrice, String name, Pageable pageable) {

        List<Product> products = queryFactory
                .selectFrom(product)
                .from(product)
                .where(searchByMaxPrice(maxPrice), searchByMinPrice(minPrice),
                        searchByName(name), searchByStore(storeId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.asc(), product.updatedAt.asc())
                .fetch();


        List<ProductGetResponseDto> responseDtos = products
                .stream()
                .map(ProductGetResponseDto::from)
                .toList();

        return new PageImpl<>(responseDtos, pageable, products.size());

    }


    BooleanExpression searchByMaxPrice(Integer maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }

    BooleanExpression searchByMinPrice(Integer minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    BooleanExpression searchByName(final String name) {
        return StringUtils.hasText(name) ? product.name.contains(name) : null;
    }

    BooleanExpression searchByStore(final UUID storeId) {
        return storeId != null ? product.storeId.eq(storeId) : null;
    }
}
