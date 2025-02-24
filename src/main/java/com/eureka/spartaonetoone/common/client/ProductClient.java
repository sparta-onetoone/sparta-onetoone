package com.eureka.spartaonetoone.common.client;

import com.eureka.spartaonetoone.common.dtos.request.ProductRequest;
import com.eureka.spartaonetoone.common.dtos.response.ProductResponse;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private static final String PRODUCTS_URI = "/api/v1/products";
    private static final String SUCCESS_CODE = "S000";
    private static final Integer MIN_QUANTITY = 1;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public CommonResponse<?> getProduct(UUID productId) throws JsonProcessingException {
        ProductResponse.Get productGetResponse = webClient
                .get()
                .uri(PRODUCTS_URI + "/{productId}", productId)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> JsonPath.read(json, "$.data"))
                .map(data -> objectMapper
                        .convertValue(data, ProductResponse.Get.class))
                .block();


        if (productGetResponse.getQuantity() < MIN_QUANTITY) {
            return CommonResponse.fail();
        }

        return CommonResponse.success(productGetResponse, "상품이 잘 조회되었습니다.");
    }

    public CommonResponse<?> reduceProduct(ProductRequest.Reduce reduce) throws JsonProcessingException {
        String statusCode = webClient
                .patch()
                .uri(PRODUCTS_URI + "/reduce-product")
                .bodyValue(reduce)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> JsonPath.read(json, "$.code"))
                .block()
                .toString();

        if (statusCode.equals(SUCCESS_CODE)) {
            return CommonResponse.success(null, null);
        }
        return CommonResponse.fail();
    }
}