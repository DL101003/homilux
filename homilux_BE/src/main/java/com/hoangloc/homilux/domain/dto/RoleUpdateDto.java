package com.hoangloc.homilux.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class RoleUpdateDto {
    private Long id;
    private String name;
    private Set<Long> permissionIds;
    private Instant updatedAt;
}