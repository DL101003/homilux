package com.hoangloc.homilux.annotation;

import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public abstract class AbstractPaginationService<E> implements PaginationService<E> {

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
        rs.setResult(page.getContent());
        return rs;
    }
}