
package com.eureka.spartaonetoone.auth.application.dtos.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthSigninResponseDto {
    private String accessToken;  // JWT 액세스 토큰
    private String refreshToken; // JWT 리프레시 토큰

    // 팩토리 메서드로 DTO 생성
    public static AuthSigninResponseDto of(String accessToken, String refreshToken) {
        return AuthSigninResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}