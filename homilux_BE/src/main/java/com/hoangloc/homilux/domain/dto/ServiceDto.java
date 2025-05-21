package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ServiceDto {
    private Long id;
    private String name;
    private Double price;
    private Long eventTypeId;
    private Instant createdAt;
    private Instant updatedAt;
}