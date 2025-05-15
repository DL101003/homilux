package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class MenuItemDto {
    private Long id;
    private String name;
    private String description;
    private String type;
    private BigDecimal price;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}