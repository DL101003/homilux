package com.hoangloc.homilux.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class ServiceItemUpdateDto {
    private Long id;
    private String name;
    private String description;
    private String eventType;
    private BigDecimal price;
    private boolean active;
    private Instant updatedAt;
}