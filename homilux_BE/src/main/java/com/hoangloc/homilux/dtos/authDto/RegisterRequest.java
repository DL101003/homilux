package com.hoangloc.homilux.dtos.authDto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phoneNumber,
        @NotNull Long roleId
) {}