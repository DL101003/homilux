package com.hoangloc.homilux.domain.dto;

import com.hoangloc.homilux.util.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class PaymentDto {
    private Long id;
    private Long eventId;
    private String transactionId;
    private double amount;
    private Instant paymentDate;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}