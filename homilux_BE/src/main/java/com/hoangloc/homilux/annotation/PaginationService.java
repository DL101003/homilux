package com.hoangloc.homilux.annotation;

import com.hoangloc.homilux.dtos.ResultPaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PaginationService<E, D> {
    ResultPaginationDto getAll(Specification<E> spec, Pageable pageable);
}