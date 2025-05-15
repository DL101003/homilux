package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
public class PaymentUpdateDto {
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private Instant paymentDate;
    private Instant updatedAt;
}