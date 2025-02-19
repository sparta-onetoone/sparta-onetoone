package com.eureka.spartaonetoone.domain.useraddress.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.eureka.spartaonetoone.domain.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(
	name = "p_user_address"
)
public class UserAddress {

	@Id
	@UuidGenerator
	@Column(name = "address_id", columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID addressId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // 사용자와의 N:1 관계

	@Column(name = "city", length = 255, nullable = false)
	private String city; // 시/도

	@Column(name = "district", length = 255, nullable = false)
	private String district; // 군/구

	@Column(name = "road_name", length = 255, nullable = false)
	private String roadName; // 도로명

	@Column(name = "zip_code", length = 50, nullable = false)
	private String zipCode; // 우편번호

	@Column(name = "detail", length = 255)
	private String detail; // 상세주소

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted; // 삭제 여부 (논리적 삭제)

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
	private LocalDateTime deletedAt;

	// 주소 정보 업데이트 메서드
	public void updateAddress(String city, String district, String loadName, String zipCode, String detail) {
		this.city = city;
		this.district = district;
		this.roadName = loadName;
		this.zipCode = zipCode;
		this.detail = detail;
		this.updatedAt = LocalDateTime.now();
	}

	// 논리적 삭제 처리 메서드
	public void deleteAddress(UUID deletedBy) {
		this.isDeleted = true;
		this.deletedBy = deletedBy;
		this.deletedAt = LocalDateTime.now();
	}

	// 사용자 설정 메서드 추가
	public void setUser(User user) {
		this.user = user;
		if (!user.getAddresses().contains(this)) {
			user.getAddresses().add(this); // 양방향 관계 편의 메서드
		}
	}
}
