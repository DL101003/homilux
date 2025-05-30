package com.hoangloc.homilux.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDto {

    @JsonProperty("access_token")
    private String accessToken;
    private long id;
    private String email;
    private String name;

}
