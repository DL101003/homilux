package com.hoangloc.homilux.dtos.authDto;

import com.hoangloc.homilux.dtos.roleDto.RoleResponse;

public record FetchAccount(
        Long id,
        String email,
        String fullName,
        RoleResponse role
) {}
