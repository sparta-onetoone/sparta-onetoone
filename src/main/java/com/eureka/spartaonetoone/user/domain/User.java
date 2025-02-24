package com.eureka.spartaonetoone.user.domain;

import com.eureka.spartaonetoone.common.utils.TimeStamp;
import com.eureka.spartaonetoone.useraddress.domain.UserAddress;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "p_user", uniqueConstraints = {@UniqueConstraint(name = "unique_email", columnNames = "email")})
public class User extends TimeStamp {
  
    @Id
    @UuidGenerator
    @Column(name = "user_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> addresses = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "default_address_id", referencedColumnName = "address_id")
    private UserAddress defaultAddress;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", length = 50, nullable = false)
    private UserGrade grade = UserGrade.SILVER;

    @Column(name = "refresh_token")
    private String refreshToken;

    // 기본 주소 설정 메서드
    public void setDefaultAddress(UserAddress address) {
      this.defaultAddress = address;
      address.setUser(this);
      if (!this.addresses.contains(address)) {
        this.addresses.add(address);
      }
    }

    // 리프레시 토큰 업데이트
    public void updateRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
    }

    // 사용자 이름 업데이트
    public void updateUsername(String username) {
      this.username = username;
    }

    // 이메일 업데이트
    public void updateEmail(String email) {
      this.email = email;
    }

    // 논리적 삭제 처리
    public void markAsDeleted(UUID deletedBy) {
      this.isDeleted = true;
      this.deletedBy = deletedBy;  // UUID를 String으로 변환하여 저장
    }

    // 권한 정보 반환
    public Collection<SimpleGrantedAuthority> getAuthorities() {
      List<SimpleGrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
      return authorities;
    }

    // 팩토리 메서드
    public static User create(String username, String email, String password, String nickname, String phoneNumber, UserRole role) {
      return User.builder()
        .username(username)
        .email(email)
        .password(password)
        .nickname(nickname)
        .phoneNumber(phoneNumber)
        .role(role)
        .grade(UserGrade.SILVER)
        .isDeleted(false)
        .addresses(new ArrayList<>())  // 기본 주소는 빈 리스트로 초기화
        .build();
    }

    // 관리자 생성 메서드
    public static User admin() {
      return User.builder()
        .username("admin")
        .role(UserRole.ADMIN)
        .isDeleted(false)
        .build();
    }
}
