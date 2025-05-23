package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PermissionDto {
    private Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private Instant createdAt;
    private Instant updatedAt;
}