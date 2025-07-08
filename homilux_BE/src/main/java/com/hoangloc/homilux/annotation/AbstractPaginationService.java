package com.hoangloc.homilux.annotation;

import com.hoangloc.homilux.dtos.ResultPaginationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.stream.Collectors;

public abstract class AbstractPaginationService<E, D> implements PaginationService<E, D> {

    protected final JpaSpecificationExecutor<E> repository;

    protected AbstractPaginationService(JpaSpecificationExecutor<E> repository) {
        this.repository = repository;
    }

    @Override
    public ResultPaginationDto getAll(Specification<E> spec, Pageable pageable) {
        Page<E> page = repository.findAll(spec, pageable);
        return new ResultPaginationDto(
                new ResultPaginationDto.Meta(
                        pageable.getPageNumber() + 1,
                        pageable.getPageSize(),
                        page.getTotalPages(),
                        page.getTotalElements()
                ),
                page.getContent().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }

    protected abstract D toResponse(E entity);
}