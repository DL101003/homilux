package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.ServicePackage;
import com.hoangloc.homilux.util.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {

    List<ServicePackage> findByEventType(EventType eventType);

    boolean existsByName(String name);
}
