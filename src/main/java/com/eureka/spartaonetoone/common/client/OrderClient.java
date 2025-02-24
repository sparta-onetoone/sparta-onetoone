package com.eureka.spartaonetoone.common.client;

import com.eureka.spartaonetoone.common.dtos.response.OrderResponse;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderClient {

    // 주문 서비스의 API 엔드포인트를 상수로 정의 (예: "/api/v1/orders")
    private static final String ORDERS_URI = "/api/v1/orders";
    // 외부 주문 API와 통신하기 위한 WebClient 빈
    private final WebClient webClient;

    public List<OrderResponse> getOrders(UUID storeId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ORDERS_URI)
                        .queryParam("store_id", storeId)
                        .build())
                .retrieve()
                .bodyToFlux(OrderResponse.class)
                .collectList()
                .block();
    }

    public UUID getStoreIdOfOrder(UUID orderId) {
        OrderResponse.GetStoreId getStoreId = webClient.get()
                .uri(ORDERS_URI + "/{order_id}", orderId)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<CommonResponse<OrderResponse.GetStoreId>>() {
                        }).map(CommonResponse::getData)
                .block();

        return getStoreId.getStoreId();
//                .bodyToMono(String.class)
//                .block();
//		return string;
//			.bodyToMono(OrderResponse.GetStoreId.class)
//			.block();
//        return null;
    }
}
