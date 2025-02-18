package com.eureka.spartaonetoone.domain.address.domain;

import com.eureka.spartaonetoone.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "p_user_address")
public class UserAddress {

    @Id
    @UuidGenerator
    @Column(name = "address_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "city", length = 255, nullable = false)
    private String city;

    @Column(name = "district", length = 255, nullable = false)
    private String district;

    @Column(name = "road_name", length = 255, nullable = false)
    private String roadName;

    @Column(name = "zip_code", length = 50, nullable = false)
    private String zipCode;

    @Column(name = "detail", length = 255)
    private String detail;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

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

    public void updateAddress(String city, String district, String loadName, String zipCode, String detail) {
        this.city = city;
        this.district = district;
        this.roadName = loadName;
        this.zipCode = zipCode;
        this.detail = detail;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteAddress(UUID deletedBy) {
        this.isDeleted = true;
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }

    public void setUser(User user) {
        this.user = user;
        if (!user.getAddresses().contains(this)) {
            user.getAddresses().add(this);
        }
    }
}
