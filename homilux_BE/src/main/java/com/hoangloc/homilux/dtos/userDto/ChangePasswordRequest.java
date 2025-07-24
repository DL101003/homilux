package com.hoangloc.homilux.dtos.userDto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {}