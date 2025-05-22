package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {
    boolean existsByNameAndEventTypeId(String name, Long eventTypeId);
}
