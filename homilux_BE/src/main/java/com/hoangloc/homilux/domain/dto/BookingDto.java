package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Getter
@Setter
public class BookingDto {
    private Long id;
    private Long userId;
    private String eventType;
    private String locationType;
    private String customLocationAddress;
    private Long servicePackageId;
    private List<Long> menuItemIds;
    private Instant eventDate;
    private String description;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}