package com.hoangloc.homilux.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hoangloc.homilux.util.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@SQLDelete(sql = "UPDATE payments SET deleted = true WHERE id = ?")
@SQLRestriction(value = "deleted = false")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private String transactionId;

    private double amount;

    private Instant paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

}