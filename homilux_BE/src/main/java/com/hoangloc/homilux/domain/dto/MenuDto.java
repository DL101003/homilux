package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class MenuDto {
    private Long id;
    private String name;
    private String description;
    private List<Long> dishIds;
    private Instant createdAt;
    private Instant updatedAt;
}