package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class DishDto {
    private Long id;
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private String category;
    private Instant createdAt;
    private Instant updatedAt;
}