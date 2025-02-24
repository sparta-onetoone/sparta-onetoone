package com.eureka.spartaonetoone.common.client;

import com.eureka.spartaonetoone.category.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryClient {

    private final WebClient webClient;

    private static final String CATEGORIES_URI = "/api/v1/categories";

    public List<Category> getCategoryByIds(List<String> categoryIds) {
        return webClient.post()
                .uri(CATEGORIES_URI + "/batch") // 엔드포인트 변경 (POST 요청)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(categoryIds) // JSON 배열로 전송
                .retrieve()
                .bodyToFlux(Category.class) // 응답을 Flux로 변환 후 List로 수집
                .collectList()
                .block();
    }
}
