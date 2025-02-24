package com.eureka.spartaonetoone.category.domain;

import com.eureka.spartaonetoone.store.domain.Store;
import jakarta.persistence.*;
import com.eureka.spartaonetoone.common.utils.TimeStamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_category")
public class Category extends TimeStamp {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Builder(builderMethodName = "builder", access = AccessLevel.PRIVATE)
    private Category(String name) {
        this.name = name;
    }

    public static Category of(String name) {
        return builder()
                .name(name)
                .build();
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public static List<Category> getDefaultCategories() {
        return Arrays.asList(
                Category.of("한식"),
                Category.of("중식"),
                Category.of("일식"),
                Category.of("양식"),
                Category.of("분식"),
                Category.of("패스트푸드")
        );
    }
}
