package com.hoangloc.homilux.domain.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class RoleDto {
    private Long id;
    private String name;
    private List<Long> permissionIds;
    private Instant createdAt;
    private Instant updatedAt;
}