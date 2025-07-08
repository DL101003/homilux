package com.hoangloc.homilux.dtos.bookingDto;


import com.hoangloc.homilux.dtos.eventTypeDto.EventTypeResponse;
import com.hoangloc.homilux.dtos.userDto.UserResponse;
import com.hoangloc.homilux.entities.enums.BookingStatus;
import com.hoangloc.homilux.entities.enums.LocationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record BookingResponse(
        Long id,
        BookingStatus status,
        Instant eventDatetime,
        Integer expectedGuests,
        LocationType locationType,
        String address,
        LocalDate contractSigningDate,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        String customerNotes,
        UserResponse user,
        EventTypeResponse eventType,
        Set<BookedServiceResponse> bookedServices,
        Instant createdAt,
        Instant updatedAt
) {}