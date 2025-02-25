package com.eureka.spartaonetoone.category.application;

import com.eureka.spartaonetoone.category.application.dtos.CategoryDto;
import com.eureka.spartaonetoone.category.application.exception.CategoryNotFoundException;
import com.eureka.spartaonetoone.category.domain.Category;
import com.eureka.spartaonetoone.category.domain.QCategory;
import com.eureka.spartaonetoone.category.domain.repository.CategoryRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(String name) {
        Category category = Category.of(name);
        return categoryRepository.save(category);
    }

    public Category updateCategory(UUID categoryId, String name) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found with ID: " + categoryId));
        category.updateName(name);
        return categoryRepository.save(category);
    }

    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found with ID: " + categoryId));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    // QueryDSL을 활용한 필터링 메서드
    public Predicate buildCategoryPredicate(String name) {
        QCategory qCategory = QCategory.category;
        BooleanExpression predicate = qCategory.isNotNull();

        if (name != null && !name.isEmpty()) {
            predicate = predicate.and(qCategory.name.containsIgnoreCase(name));
        }
        return predicate;
    }

    // categoryIds를 이용한 검색 메서드 (핵심 요구사항
    public Page<CategoryDto> searchCategoriesByIds(List<UUID> categoryIds, Pageable pageable) {
        return categoryRepository.searchCategories(categoryIds, pageable);
    }
}


