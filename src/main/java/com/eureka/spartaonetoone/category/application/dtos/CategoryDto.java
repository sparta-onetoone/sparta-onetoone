package com.eureka.spartaonetoone.category.application.dtos;

import com.eureka.spartaonetoone.category.domain.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CategoryDto {
    private UUID id;
    private String name;

    // 수동으로 all-args 생성자 추가
    public CategoryDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryDto from(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
