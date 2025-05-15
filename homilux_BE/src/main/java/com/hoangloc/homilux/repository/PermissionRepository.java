package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByIdAndDeletedFalse(Long id);

    List<Permission> findAllByDeletedFalse();

    boolean existsByNameAndDeletedFalse(String name);
}