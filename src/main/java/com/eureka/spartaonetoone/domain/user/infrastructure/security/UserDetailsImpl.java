package com.eureka.spartaonetoone.domain.user.infrastructure.security;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eureka.spartaonetoone.domain.user.domain.User;

import lombok.Getter;

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

	// getUser() 메서드 추가

	// 정적 메서드 추가: User 객체에서 UserDetailsImpl 생성
	public static UserDetailsImpl fromUser(User user) {
		return new UserDetailsImpl(user);
	}
}
