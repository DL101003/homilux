package com.hoangloc.homilux.dtos.bookingDto;

import java.math.BigDecimal;

public record BookedServiceResponse(
        Long serviceId,
        String serviceName,
        int quantity,
        BigDecimal priceAtBooking,
        BigDecimal lineTotal
) {}