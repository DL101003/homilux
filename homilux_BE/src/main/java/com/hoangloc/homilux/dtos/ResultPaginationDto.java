package com.hoangloc.homilux.dtos;

public record ResultPaginationDto(
        Meta meta,
        Object result
) {
    public record Meta(
            int page,
            int pageSize,
            int pages,
            long total
    ) {}
}