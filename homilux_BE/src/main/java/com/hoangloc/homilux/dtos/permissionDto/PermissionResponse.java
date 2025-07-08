package com.hoangloc.homilux.dtos.permissionDto;

import com.hoangloc.homilux.entities.enums.HttpMethod;

public record PermissionResponse(
        Long id,
        String name,
        String apiPath,
        HttpMethod method,
        String module
) {}