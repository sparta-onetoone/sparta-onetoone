package com.eureka.spartaonetoone.domain.user.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.eureka.spartaonetoone.domain.auth.application.dtos.request.AuthSignupRequestDto;
import com.eureka.spartaonetoone.domain.user.infrastructure.security.UserDetailsImpl;
import com.eureka.spartaonetoone.domain.useraddress.domain.UserAddress;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_user", uniqueConstraints = {@UniqueConstraint(name = "unique_email", columnNames = "email")})
public class User {

	@Id
	@UuidGenerator
	@Column(name = "user_id", columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID userId;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserAddress> addresses = new ArrayList<>(); // 사용자와 주소의 1:N 관계

	@Column(name = "username", length = 50, nullable = false)
	private String username;

	@Column(name = "email", length = 50, unique = true, nullable = false)
	private String email;

	@Column(name = "password", length = 255, nullable = false)
	private String password;

	@Column(name = "nickname", length = 50, nullable = false)
	private String nickname;

	@Column(name = "phone_number", length = 50, nullable = false)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private UserRole role;

	// 권한 반환 메서드 추가
	public List<SimpleGrantedAuthority> getAuthorities() {
		// UserRole을 SimpleGrantedAuthority로 변환
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "grade", length = 50, nullable = false)
	private UserGrade grade = UserGrade.SILVER;

	@Column(name = "refresh_token")
	private String refreshToken; // 리프레시 토큰 저장

	@Column(name = "created_by", columnDefinition = "UUID")
	private UUID createdBy;

	@Column(name = "updated_by", columnDefinition = "UUID")
	private UUID updatedBy;

	@Column(name = "deleted_by", columnDefinition = "UUID")
	private UUID deletedBy;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt; // 계정 삭제 일자

	// 리프레시 토큰 업데이트
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	// 비밀번호 업데이트
	public void updatePassword(String password) {
		this.password = password;
	}

	// 사용자 이름 업데이트
	public void updateUsername(String username) {
		this.username = username;
	}

	// 이메일 업데이트
	public void updateEmail(String email) {
		this.email = email;
	}

	// 주소 추가 편의 메서드
	public void addAddress(UserAddress address) {
		addresses.add(address);
		address.setUser(this); // 양방향 관계 설정
	}

	// 논리적 삭제 처리 메서드 (Setter 역할 수행)
	public void markAsDeleted(UUID deletedBy) {
		this.isDeleted = true;
		this.deletedBy = deletedBy;
		this.deletedAt = LocalDateTime.now();
	}

	// 회원가입 요청으로부터 User 생성 (from 메서드)
	public static User from(AuthSignupRequestDto request, PasswordEncoder passwordEncoder) {
		return User.builder()
			.userId(UUID.randomUUID())
			.username(request.getUsername())
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.nickname(request.getNickname())
			.phoneNumber(request.getPhoneNumber())
			.role(UserRole.valueOf(request.getRole().toUpperCase()))
			.grade(UserGrade.SILVER)
			.isDeleted(false) // 삭제 여부 기본값 설정
			.createdAt(LocalDateTime.now()) // 생성 시간 설정
			.updatedAt(LocalDateTime.now()) // 수정 시간 설정
			.build();
	}

	// UserDetailsImpl 객체 반환
	public UserDetailsImpl toUserDetails() {
		return new UserDetailsImpl(this); // User 객체를 UserDetailsImpl로 변환
	}
}