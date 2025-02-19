package com.eureka.spartaonetoone.store.domain.entity;

import jakarta.persistence.*;
import com.eureka.spartaonetoone.common.utils.TimeStamp;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_store_address")
public class Address extends TimeStamp {

	@Id
	@GeneratedValue
	@Column(name = "adress_id")
	private UUID id;

	// Address가 소유자로서 Store와 1:1 연관관계로 연결됨
	@OneToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "city", length = 255, nullable = false)
	private String city;

	@Column(name = "district", length = 255, nullable = false)
	private String district;

	@Column(name = "load_name", length = 255, nullable = false)
	private String loadName;

	@Column(name = "zip_code", length = 255, nullable = false)
	private String zipCode;

	@Column(name = "detail", length = 255, nullable = false)
	private String detail;

	// Private 생성자: 오직 정적 팩토리 메서드와 내부 빌더를 통해서만 객체 생성
	@Builder(builderMethodName = "builder", access = AccessLevel.PRIVATE)
	private Address(Store store, String city, String district, String loadName, String zipCode, String detail) {
		this.store = store;
		this.city = city;
		this.district = district;
		this.loadName = loadName;
		this.zipCode = zipCode;
		this.detail = detail;
	}

	// 정적 팩토리 메서드
	public static Address from(Store store, String city, String district, String loadName, String zipCode, String detail) {
		return Address.builder()
			.store(store)
			.city(city)
			.district(district)
			.loadName(loadName)
			.zipCode(zipCode)
			.detail(detail)
			.build();
	}

	// 엔티티 내부 업데이트 메서드
	public void updateFrom(String city, String district, String loadName, String zipCode, String detail) {
		this.city = city;
		this.district = district;
		this.loadName = loadName;
		this.zipCode = zipCode;
		this.detail = detail;
	}
}
