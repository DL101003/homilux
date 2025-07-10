package com.hoangloc.homilux.entities;

import com.hoangloc.homilux.entities.enums.PaymentMethod;
import com.hoangloc.homilux.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod; // CASH, BANK_TRANSFER, THIRD_PARTY_GATEWAY

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, SUCCESSFUL, FAILED

    @Column(name = "payment_date")
    private Instant paymentDate;

    @Column(name = "transaction_id")
    private String transactionId;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
}