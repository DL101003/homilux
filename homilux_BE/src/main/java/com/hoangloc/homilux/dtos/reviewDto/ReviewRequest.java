package com.hoangloc.homilux.dtos.reviewDto;

public record ReviewRequest(
        Byte rating,
        String comment
) {}