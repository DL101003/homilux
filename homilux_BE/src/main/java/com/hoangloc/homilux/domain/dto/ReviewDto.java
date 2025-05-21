package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ReviewDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long dishId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
}