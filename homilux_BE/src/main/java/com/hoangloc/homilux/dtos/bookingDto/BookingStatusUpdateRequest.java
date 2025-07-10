package com.hoangloc.homilux.dtos.bookingDto;

import com.hoangloc.homilux.entities.enums.BookingStatus;

public record BookingStatusUpdateRequest(BookingStatus status) {}