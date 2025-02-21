package com.eureka.spartaonetoone.user.infrastructure.security;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eureka.spartaonetoone.user.domain.User;

import lombok.Getter;

@Getter
public class UserDetailsImpl implements UserDetails {

	private final UUID userId;
	private final String username;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;
	private final User user;

	// 기존 생성자: User 객체를 이용해 UserDetailsImpl 생성
	public UserDetailsImpl(User user) {
		this.userId = user.getUserId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.authorities = user.getAuthorities();
		this.user = user;
	}

	// 새로운 생성자: 내부 요청용
	public UserDetailsImpl(UUID userId, String username, String role) {
		this.userId = userId;
		this.username = username;
		this.password = null;  // 내부 요청에서는 비밀번호가 필요하지 않음
		this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
		this.user = null;  // User 객체가 없을 수 있음
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

	// 정적 메서드: User 객체를 기반으로 UserDetailsImpl 생성
	public static UserDetailsImpl fromUser(User user) {
		return new UserDetailsImpl(user);
	}

	public static UserDetailsImpl adminUser() {
		return new UserDetailsImpl(User.admin());

	}
}
