package com.eureka.spartaonetoone.ai.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eureka.spartaonetoone.ai.domain.Ai;

public interface AiRepository extends JpaRepository<Ai, UUID> {
}
