package com.eureka.spartaonetoone.payment.domain.repository;

import com.eureka.spartaonetoone.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, CustomPaymentRepository {
}
