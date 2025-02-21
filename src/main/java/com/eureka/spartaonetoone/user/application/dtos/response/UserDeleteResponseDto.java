package com.eureka.spartaonetoone.user.application.dtos.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDeleteResponseDto {
    private String userId;
}
