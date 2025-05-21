package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class EventTypeDto {
    private Long id;

    private String name;

    private String description;

    private Instant createdAt;
    private Instant updatedAt;
}