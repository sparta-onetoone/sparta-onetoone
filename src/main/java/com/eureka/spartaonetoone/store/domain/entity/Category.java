package com.eureka.spartaonetoone.store.domain.entity;

import jakarta.persistence.*;
import com.eureka.spartaonetoone.common.utils.TimeStamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

	// Private 생성자 : 오직 정적 팩토리 메서드나 빌더를 통해서만 객체를 생성
	@Builder(builderMethodName = "builder", access = AccessLevel.PRIVATE)
	private Category(String name) {
		this.name = name;
	}

	// 정적 팩토리 메서드
	public static Category of(String name) {
		return builder()
			.name(name)
			.build();
	}

	// 기본 카테고리 목록을 반환 - 분류값 미리 삽입
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
