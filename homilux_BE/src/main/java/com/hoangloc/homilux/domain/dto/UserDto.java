package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private Instant createdAt;
    private Instant updatedAt;
}
