package com.hoangloc.homilux.dtos.authDto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenResponse(@JsonProperty("access_token") String accessToken) {}