package com.eureka.spartaonetoone.user.infrastructure.security;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import com.eureka.spartaonetoone.user.domain.User;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
        this.user = user;
    }

    public UserDetailsImpl(User user, UUID userId) {
        this.userId = userId;
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities();
        this.user = user;
    }

    public static UserDetailsImpl fromUser(User user) {
        return new UserDetailsImpl(user);
    }

    public static UserDetailsImpl testUser(User user, UUID userId) {
        return new UserDetailsImpl(user, userId);
    }

    public static UserDetailsImpl adminUser() {
        return new UserDetailsImpl(User.admin());
    }

    public static UserDetailsImpl connectUser(UUID userId) {
        return new UserDetailsImpl(User.admin(), userId);
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
}
