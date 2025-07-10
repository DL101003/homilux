package com.hoangloc.homilux.dtos.roleDto;


import com.hoangloc.homilux.dtos.permissionDto.PermissionResponse;

import java.util.List;

public record RoleResponse(
        Long id,
        String name,
        List<PermissionResponse> permissionIds
) {}