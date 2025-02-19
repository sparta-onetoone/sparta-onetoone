package com.eureka.spartaonetoone.domain.user.application.dtos.response;

import com.eureka.spartaonetoone.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponseDto {
    private String userId;
    private String username;
    private String email;
    private String role;

    public static UserDetailResponseDto from(User user) {
        return UserDetailResponseDto.builder()
                .userId(user.getUserId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
