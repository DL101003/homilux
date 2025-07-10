package com.hoangloc.homilux.dtos;

import java.time.Instant;

public record UploadFileDto(
        String name,
        Instant uploadedAt
) {}
