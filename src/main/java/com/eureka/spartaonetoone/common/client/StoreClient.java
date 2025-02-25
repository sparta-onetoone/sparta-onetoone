package com.eureka.spartaonetoone.common.client;

import com.eureka.spartaonetoone.common.dtos.response.StoreResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreClient {
    private static final String STORE_URI = "/api/v1/stores";
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public UUID getOwnerId(UUID storeId) {
        StoreResponse.GetOwnerId getOwnerId = webClient.get()
                .uri(STORE_URI + "/{store_id}", storeId)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> JsonPath.read(body, "$"))
                .map(data -> objectMapper.convertValue(data, StoreResponse.GetOwnerId.class))
                .block();

        return getOwnerId.getOwnerId();
    }
}
