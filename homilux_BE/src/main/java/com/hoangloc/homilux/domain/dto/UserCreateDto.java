package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserCreateDto {
    private Long id;
    private String username;
    private String email;
    private Instant createdAt;
}