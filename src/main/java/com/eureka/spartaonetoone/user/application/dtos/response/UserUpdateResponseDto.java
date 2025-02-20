package com.eureka.spartaonetoone.user.application.dtos.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateResponseDto {
    private String userId;
    private String username;
    private String email;
}
