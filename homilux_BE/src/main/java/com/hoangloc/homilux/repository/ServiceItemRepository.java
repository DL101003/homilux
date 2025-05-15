package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    Optional<ServiceItem> findByIdAndDeletedFalse(Long id);

    List<ServiceItem> findAllByDeletedFalse();

    boolean existsByNameAndDeletedFalse(String name);
}