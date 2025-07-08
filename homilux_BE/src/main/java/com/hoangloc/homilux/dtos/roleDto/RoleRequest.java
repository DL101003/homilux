package com.hoangloc.homilux.dtos.roleDto;

import java.util.List;

public record RoleRequest(
        String name,
        List<Long> permissionIds
) {}