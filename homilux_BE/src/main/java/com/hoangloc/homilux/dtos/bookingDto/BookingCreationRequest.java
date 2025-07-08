package com.hoangloc.homilux.dtos.bookingDto;


import com.hoangloc.homilux.entities.enums.LocationType;

import java.time.Instant;
import java.util.List;

public record BookingCreationRequest(
        Long eventTypeId,
        Instant eventDatetime,
        Integer expectedGuests,
        LocationType locationType,
        String address,
        String customerNotes,
        List<BookedServiceRequest> services
) {}