package com.hoangloc.homilux.dtos.reviewDto;


import com.hoangloc.homilux.dtos.userDto.UserResponse;

import java.time.Instant;
import java.util.Set;

public record ReviewResponse(
        Long id,
        byte rating,
        String comment,
        Long bookingId,
        UserResponse user,
        Set<ReviewImageResponse> images,
        Instant createdAt
) {}