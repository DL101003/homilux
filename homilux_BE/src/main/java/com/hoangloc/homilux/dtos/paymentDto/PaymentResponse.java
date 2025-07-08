package com.hoangloc.homilux.dtos.paymentDto;

import com.hoangloc.homilux.entities.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Instant paymentDate,
        String transactionId,
        String notes
) {}