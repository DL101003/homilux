package com.hoangloc.homilux.domain;

import com.hoangloc.homilux.util.PaymentMethod;
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

    @OneToOne(mappedBy = "payment")
    private Event event;

    private String transactionId;

    private double amount;

    private Instant paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, COMPLETED

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // ONLINE, POST_EVENT

    private String paymentProvider; // VNPAY, MOMO,...

    @Column(columnDefinition = "MEDIUMTEXT")
    private String paymentUrl;

}