package com.hoangloc.homilux.dtos.paymentDto;

import com.hoangloc.homilux.entities.enums.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String transactionId,
        String notes
) {}