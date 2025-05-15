package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.ServicePackage;
import com.hoangloc.homilux.util.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    Optional<ServicePackage> findByIdAndDeletedFalse(Long id);

    List<ServicePackage> findAllByDeletedFalse();

    List<ServicePackage> findByEventTypeAndDeletedFalse(EventType eventType);

    boolean existsByNameAndDeletedFalse(String name);
}
