package com.eureka.spartaonetoone.common.utils;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeStamp {

	@CreatedDate
	protected LocalDateTime createdAt;

	@CreatedBy
	protected String createdBy;

	@LastModifiedDate
	protected LocalDateTime updatedAt;

	@LastModifiedBy
	protected String updatedBy;

	protected LocalDateTime deletedAt;

	protected String deletedBy;

}
