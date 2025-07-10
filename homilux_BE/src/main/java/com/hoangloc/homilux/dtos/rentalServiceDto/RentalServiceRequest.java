package com.hoangloc.homilux.dtos.rentalServiceDto;

import com.hoangloc.homilux.entities.enums.ServiceType;

import java.math.BigDecimal;

public record RentalServiceRequest(
        String name,
        String description,
        ServiceType type,
        BigDecimal price,
        boolean isCustomizable
) {}