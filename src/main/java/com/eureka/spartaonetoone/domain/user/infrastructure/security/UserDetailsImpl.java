package com.eureka.spartaonetoone.domain.user.infrastructure.security;

import com.eureka.spartaonetoone.domain.user.domain.User;
import com.eureka.spartaonetoone.domain.user.domain.UserGrade;
import com.eureka.spartaonetoone.domain.user.domain.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final UUID userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // 생성자에서 User 객체를 받도록 수정
    public UserDetailsImpl(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities(); // User 엔티티의 getAuthorities() 메서드 호출
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // UserDetailsImpl 객체 반환 (User 객체를 통해 생성)
    public static UserDetailsImpl fromUser(User user) {
        return new UserDetailsImpl(user);
    }

    // UserDetailsImpl 객체에서 User 객체를 반환하는 메서드
    public User toUser() {
        return User.builder()
                .userId(userId) // 사용자 ID
                .username(username) // 사용자 이름
                .password(password) // 비밀번호
                .isDeleted(false) // 삭제 여부 기본값
                .role(UserRole.CUSTOMER) // 기본 역할 (예시)
                .grade(UserGrade.SILVER) // 기본 등급 (예시)
                .createdAt(LocalDateTime.now()) // 생성 시간
                .updatedAt(LocalDateTime.now()) // 수정 시간
                .createdBy(null) // 생성자 ID (필요 시 설정)
                .updatedBy(null) // 수정자 ID (필요 시 설정)
                .deletedBy(null) // 삭제자 ID (필요 시 설정)
                .addresses(new ArrayList<>()) // 빈 주소 리스트 초기화
                .build();
    }
}