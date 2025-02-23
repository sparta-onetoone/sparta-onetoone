package com.eureka.spartaonetoone.user.application.dtos.response;

import com.eureka.spartaonetoone.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserListResponseDto {
    private String userId;
    private String username;
    private String email;

    public static UserListResponseDto from(User user) {
        return UserListResponseDto.builder()
                .userId(user.getUserId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}