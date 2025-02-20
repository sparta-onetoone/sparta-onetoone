// package com.eureka.spartaonetoone.common.client;
//
// import java.util.UUID;
//
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.data.domain.Page;
// import org.springframework.stereotype.Component;
//
// import com.eureka.spartaonetoone.common.utils.CommonResponse;
// import com.eureka.spartaonetoone.domain.user.application.dtos.response.UserDetailResponseDto;
// import com.eureka.spartaonetoone.domain.user.application.dtos.response.UserListResponseDto;
//
// import lombok.RequiredArgsConstructor;
//
// @Component
// @RequiredArgsConstructor
// public class UserClient {
//
// 	private final WebClient webClient;
// 	private static final String BASE_URL = "/api/v1/users"; // API 엔드포인트 기본 URL
//
// 	// 회원 단건 조회
// 	public Mono<CommonResponse<UserDetailResponseDto>> getUserDetail(UUID userId) {
// 		return webClient.get()
// 			.uri(BASE_URL + "/{userId}", userId)
// 			.retrieve()
// 			.bodyToMono(new ParameterizedTypeReference<>() {
// 			});
// 	}
//
// 	// 회원 전체 조회
// 	public Mono<CommonResponse<Page<UserListResponseDto>>> getAllUsers(int page, int size) {
// 		return webClient.get()
// 			.uri(uriBuilder -> uriBuilder
// 				.path(BASE_URL)
// 				.queryParam("page", page)
// 				.queryParam("size", size)
// 				.build())
// 			.retrieve()
// 			.bodyToMono(new ParameterizedTypeReference<>() {
// 			});
// 	}
//
// }
