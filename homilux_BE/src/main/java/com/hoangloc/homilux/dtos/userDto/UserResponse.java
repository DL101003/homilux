package com.hoangloc.homilux.dtos.userDto;

import com.hoangloc.homilux.entities.enums.AuthProvider;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        AuthProvider authProvider,
        String phoneNumber,
        Long roleId,
        String roleName
) {}