package com.eureka.spartaonetoone.category.domain.repository;

import com.eureka.spartaonetoone.category.domain.Category;

import java.util.List;

public interface CustomCategoryRepository {
    List<Category> findCategoriesByName(String name);
}
