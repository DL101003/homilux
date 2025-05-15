package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ServicePackageCreateDto {
    private Long id;
    private String name;
    private String description;
    private String eventType;
    private List<Long> serviceItemIds;
    private BigDecimal price;
    private boolean active;
    private Instant createdAt;
}