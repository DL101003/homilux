package com.hoangloc.homilux.dtos.authDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangloc.homilux.dtos.roleDto.RoleResponse;

public record LoginResponse (
        @JsonProperty("access_token") String accessToken,
        Long id,
        String email,
        String fullName,
        RoleResponse role
) {}

