package com.eureka.spartaonetoone.category.presentation;

import com.eureka.spartaonetoone.category.application.CategoryService;
import com.eureka.spartaonetoone.category.application.dtos.CategoryDto;
import com.eureka.spartaonetoone.category.application.exception.CategoryNotFoundException;
import com.eureka.spartaonetoone.category.domain.Category;
import com.eureka.spartaonetoone.category.domain.repository.CategoryRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

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

    @GetMapping("/filter")
    public ResponseEntity<List<Category>> getCategoriesByFilter(@RequestParam(required = false) String name) {
        Predicate predicate = categoryService.buildCategoryPredicate(name);
        List<Category> categories = (List<Category>) categoryRepository.findAll(predicate);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<CategoryDto>> searchCategories(@RequestBody List<UUID> categoryIds, Pageable pageable) {    //categoryService.searchCategories(categoryIds);
//        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryService.searchCategoriesByIds(categoryIds, pageable));
    }
}
