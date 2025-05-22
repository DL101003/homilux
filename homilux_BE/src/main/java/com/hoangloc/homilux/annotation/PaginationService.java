package com.hoangloc.homilux.annotation;

import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PaginationService<E, D> {
    ResultPaginationDTO getAll(Specification<E> spec, Pageable pageable);
}