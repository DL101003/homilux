package com.hoangloc.homilux.dtos.authDto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse (
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        String email,
        String fullName,
        Long roleId,
        String roleName
) {}

