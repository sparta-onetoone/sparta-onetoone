package com.eureka.spartaonetoone.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080") // 기본 URL 설정
            // .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0ZGU3OTM2ZC0zMWFiLTRlZWQtYTZiOC04NzQyNmE2NzdhN2QiLCJlbWFpbCI6InVzZXJAZXhhbXBsZS5jb20iLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3NDAxMjE4MDUsImV4cCI6MTc0MDEyNTQwNX0.T6KWHjI_fSn31-Zo1e3tbSEYx6Lvsefl5bMcK_cmstU")
            .defaultHeader("X-Client-Credential", "onetoone")
            .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}