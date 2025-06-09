package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    boolean existsByTransactionId(String transactionId);
    Optional<Payment> findByTransactionId(String transactionId);
}