package com.eureka.spartaonetoone.category.infrastructure;

import com.eureka.spartaonetoone.category.application.dtos.CategoryDto;
import com.eureka.spartaonetoone.category.domain.Category;
import com.eureka.spartaonetoone.category.domain.repository.CustomCategoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static com.eureka.spartaonetoone.category.domain.QCategory.category;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CustomCategoryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CategoryDto> searchCategories(List<UUID> categoryIds, Pageable pageable) {
        List<Category> categories = queryFactory
                .selectFrom(category)
                .where(category.id.in(categoryIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<CategoryDto> dtos = categories.stream()
                .map(CategoryDto::from)
                .collect(Collectors.toList());

        return new PageImpl<CategoryDto>(dtos, pageable, dtos.size());
    }
}
