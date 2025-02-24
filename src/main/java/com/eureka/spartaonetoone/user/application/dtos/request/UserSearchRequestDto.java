package com.eureka.spartaonetoone.user.application.dtos.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserSearchRequestDto {
	private String username;
	private String email;

	@Size(max = 50)
	private String sortBy; // 정렬 기준 (createdDate 또는 updatedDate)

	@NotNull(message = "Page size is required")
	private Integer pageSize; // 페이지 크기 (10, 30, 50 중 하나)

	// 페이지 크기 설정 메서드
	public void setPageSize(Integer pageSize) {
		if (pageSize == null || (pageSize != 10 && pageSize != 30 && pageSize != 50)) {
			this.pageSize = 10; // 기본값으로 10 설정
		} else {
			this.pageSize = pageSize;
		}
	}

	// 기본 생성자에서 pageSize 초기화
	public UserSearchRequestDto() {
		this.pageSize = 10; // 기본값 설정
	}
}
