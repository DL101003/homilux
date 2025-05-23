package com.hoangloc.homilux.domain.dto;

import com.hoangloc.homilux.util.PaymentMethod;
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
    private PaymentMethod paymentMethod;
    private String paymentProvider;
    private String paymentUrl;
    private Instant createdAt;
    private Instant updatedAt;
}