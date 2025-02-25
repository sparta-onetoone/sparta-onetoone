package com.eureka.spartaonetoone.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.eureka.spartaonetoone.category.application.dtos.CategoryDto;
import com.eureka.spartaonetoone.category.application.exception.CategoryNotFoundException;
import com.eureka.spartaonetoone.category.domain.Category;
import com.eureka.spartaonetoone.category.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCategory() {
        // given
        String name = "한식";
        Category category = Category.of(name);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // when
        Category created = categoryService.createCategory(name);

        // then
        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(name);
    }

    @Test
    public void testUpdateCategory_Success() {
        // given
        UUID categoryId = UUID.randomUUID();
        String originalName = "한식";
        String newName = "일식";
        Category existing = Category.of(originalName);
        // 목에서 기존 카테고리 반환
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        // 목에서 수정 후 저장된 카테고리 반환
        when(categoryRepository.save(existing)).thenReturn(existing);

        // when
        Category updated = categoryService.updateCategory(categoryId, newName);

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo(newName);
    }

    @Test
    public void testUpdateCategory_NotFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        String newName = "일식";
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, newName))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("No category found with ID: " + categoryId);
    }

    @Test
    public void testGetCategoryById_Success() {
        // given
        UUID categoryId = UUID.randomUUID();
        String name = "한식";
        Category category = Category.of(name);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // when
        Category found = categoryService.getCategoryById(categoryId);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCategoryById_NotFound() {
        // given
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> categoryService.getCategoryById(categoryId))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("No category found with ID: " + categoryId);
    }

    @Test
    public void testGetAllCategories() {
        // given
        Category cat1 = Category.of("한식");
        Category cat2 = Category.of("중식");
        List<Category> categories = Arrays.asList(cat1, cat2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // when
        List<Category> result = categoryService.getAllCategories();

        // then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        List<String> names = result.stream().map(Category::getName).collect(Collectors.toList());
        assertThat(names).containsExactlyInAnyOrder("한식", "중식");
    }

    @Test
    public void testBuildCategoryPredicate() {
        // given
        String searchName = "한";
        // when
        // buildCategoryPredicate 메서드는 QueryDSL Predicate를 반환하므로,
        // 반환값이 null이 아니고, 조건이 올바르게 구성되었음을 간단히 검증
        assertThat(categoryService.buildCategoryPredicate(searchName)).isNotNull();
    }

    @Test
    public void testSearchCategoriesByIds() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(id1, id2);

        Category cat1 = Category.of("한식");
        Category cat2 = Category.of("중식");
        List<Category> categories = Arrays.asList(cat1, cat2);
        // CategoryDto로 변환
        List<CategoryDto> dtos = categories.stream().map(CategoryDto::from).collect(Collectors.toList());
        Page<CategoryDto> page = new PageImpl<>(dtos);

        when(categoryRepository.searchCategories(ids, Pageable.unpaged())).thenReturn(page);

        // when
        Page<CategoryDto> result = categoryService.searchCategoriesByIds(ids, Pageable.unpaged());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        List<String> names = result.getContent().stream().map(CategoryDto::getName).collect(Collectors.toList());
        assertThat(names).contains("한식", "중식");
    }
}
