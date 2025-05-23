package com.hoangloc.homilux.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class UploadFileDto {
    private String name;
    private Instant uploadedAt;
}
