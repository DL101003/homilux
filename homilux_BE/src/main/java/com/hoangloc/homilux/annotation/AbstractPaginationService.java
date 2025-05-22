package com.hoangloc.homilux.annotation;

import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
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
    public ResultPaginationDTO getAll(Specification<E> spec, Pageable pageable) {
        Page<E> page = repository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
        return rs;
    }

    protected abstract D toDto(E entity);
}