package com.eureka.spartaonetoone.domain.user.domain;

import com.eureka.spartaonetoone.domain.auth.application.exception.AuthException;

import lombok.Getter;

@Getter
public enum UserRole {
	CUSTOMER("ROLE_CUSTOMER"),
	OWNER("ROLE_OWNER"),
	ADMIN("ROLE_ADMIN");

	private final String authority;

	UserRole(String authority) {
		this.authority = authority;
	}

	public static UserRole of(String role) {
		try {
			return UserRole.valueOf(role.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException.InvalidRoleException(role);
		}
	}
}
