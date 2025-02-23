package com.eureka.spartaonetoone.user.infrastructure.security;

import com.eureka.spartaonetoone.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final UUID userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    @Getter
    private final User user;

    // 생성자
    public UserDetailsImpl(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities();
        this.user = user;// User 엔티티의 권한 목록 반환
    }


    // 정적 메서드 추가: User 객체에서 UserDetailsImpl 생성
    public static UserDetailsImpl fromUser(User user) {
        return new UserDetailsImpl(user);
    }

    public static UserDetailsImpl adminUser() {
        return new UserDetailsImpl(User.admin());
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

    // getUser() 메서드 추가

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
