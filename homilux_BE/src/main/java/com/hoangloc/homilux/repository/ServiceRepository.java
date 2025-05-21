package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    boolean existsByNameAndEventTypeId(String name, Long eventTypeId);
}
