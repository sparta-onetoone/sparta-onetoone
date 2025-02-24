package com.eureka.spartaonetoone.category.domain.repository;

import com.eureka.spartaonetoone.category.application.dtos.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CustomCategoryRepository {
    Page<CategoryDto> searchCategories(List<UUID> categoryIds, Pageable pageable);
}