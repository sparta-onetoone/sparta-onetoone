package com.eureka.spartaonetoone.category.domain.repository;

import com.eureka.spartaonetoone.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>, QuerydslPredicateExecutor<Category>, CustomCategoryRepository{
}
