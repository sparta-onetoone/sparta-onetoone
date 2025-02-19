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
	// User 객체를 생성하는 메서드
	public User toUser() {
		// User.of()를 통해 User 객체를 생성
		return User.of(userId, username, password);
	}
}
