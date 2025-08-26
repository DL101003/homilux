package com.hoangloc.homilux.dtos.permissionDto;

public record PermissionResponse(
        Long id,
        String name,
        String apiPath,
        String method,
        String module
) {}