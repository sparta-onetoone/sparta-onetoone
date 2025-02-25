package com.eureka.spartaonetoone.category.presentation;

import com.eureka.spartaonetoone.category.application.CategoryService;
import com.eureka.spartaonetoone.category.application.dtos.CategoryDto;
import com.eureka.spartaonetoone.category.application.exception.CategoryNotFoundException;
import com.eureka.spartaonetoone.category.domain.Category;
import com.eureka.spartaonetoone.category.domain.repository.CategoryRepository;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    // 카테고리 생성
    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<?>> createCategory(@RequestParam String name) {
        Category category = Category.of(name);
        Category saved = categoryRepository.save(category);
        // CategoryDto로 변환 (변환 메서드가 있다면 사용)
        CategoryDto dto = CategoryDto.from(saved);
        return ResponseEntity.ok(CommonResponse.success(dto, "카테고리 생성 성공"));
    }

    // 카테고리 수정
    @Override
    @PutMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<?>> updateCategory(@PathVariable UUID categoryId, @RequestParam String name) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found with ID: " + categoryId));
        category.updateName(name);
        Category updated = categoryRepository.save(category);
        CategoryDto dto = CategoryDto.from(updated);
        return ResponseEntity.ok(CommonResponse.success(dto, "카테고리 수정 성공"));
    }

    // 이름 필터를 사용한 카테고리 조회
    @Override
    @GetMapping("/filter")
    public ResponseEntity<CommonResponse<?>> getCategoriesByFilter(@RequestParam(required = false) String name) {
        // Predicate를 사용한 필터링은 기존 방식 그대로 사용
        // 반환 타입을 CommonResponse로 감싸서 반환
        @SuppressWarnings("unchecked")
        List<Category> categories = (List<Category>) categoryRepository.findAll(categoryService.buildCategoryPredicate(name));
        // 필요 시 CategoryDto로 매핑
        List<CategoryDto> dtos = categories.stream()
                .map(CategoryDto::from)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(dtos, "필터 카테고리 조회 성공"));
    }

    // 카테고리 단건 조회
    @Override
    @GetMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<?>> getCategoryById(@PathVariable UUID categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        CategoryDto dto = CategoryDto.from(category);
        return ResponseEntity.ok(CommonResponse.success(dto, "카테고리 조회 성공"));
    }

    // 전체 카테고리 조회
    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<?>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDto> dtos = categories.stream()
                .map(CategoryDto::from)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(dtos, "전체 카테고리 조회 성공"));
    }

    // 카테고리 검색: 여러 UUID 목록을 기반으로 페이징 처리된 CategoryDto 반환
    @Override
    @PostMapping("/search")
    public ResponseEntity<CommonResponse<?>> searchCategories(@RequestBody List<UUID> categoryIds, Pageable pageable) {
        Page<CategoryDto> page = categoryService.searchCategoriesByIds(categoryIds, pageable);
        return ResponseEntity.ok(CommonResponse.success(page, "카테고리 검색 성공"));
    }
}
