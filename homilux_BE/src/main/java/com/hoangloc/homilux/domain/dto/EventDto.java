package com.hoangloc.homilux.domain.dto;

import com.hoangloc.homilux.util.LocationType;
import com.hoangloc.homilux.util.PaymentMethod;
import com.hoangloc.homilux.util.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class EventDto {
    private Long id;
    private Long userId;
    private Long eventTypeId;
    private Long menuId;
    private LocationType locationType;
    private String customLocation;
    private int estimatedAttendees;
    private Instant eventDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDate contractDate;
    private Instant createdAt;
    private Instant updatedAt;
}