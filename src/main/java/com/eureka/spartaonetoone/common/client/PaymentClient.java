package com.eureka.spartaonetoone.common.client;

import java.util.UUID;

import com.eureka.spartaonetoone.common.dtos.request.PaymentRequest;
import com.eureka.spartaonetoone.common.utils.CommonResponse;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private static final String PAYMENTS_URI = "/api/v1/payments";
    private final WebClient webClient;

    public CommonResponse<?> createPayment(PaymentRequest.Create request, UUID userId) {
        String statusCode = webClient.post()
                .uri(PAYMENTS_URI)
                .bodyValue(request)
                .header("X-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> JsonPath.read(json, "$.code"))
                .block()
                .toString();

        if (statusCode.equals("S000")) {
            return CommonResponse.success(null, null);
        }
        return CommonResponse.fail();

    }
}
