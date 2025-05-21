package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Payment;
import com.hoangloc.homilux.util.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findPaymentsByStatus(String paymentStatus);
    boolean existsByTransactionId(String transactionId);
}