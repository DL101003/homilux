package com.hoangloc.homilux.dtos.authDto;

public record RegisterRequest(
        String fullName,
        String email,
        String password,
        String phoneNumber,
        Long roleId
) {}