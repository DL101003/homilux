package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    boolean existsByName(String name);
    boolean existsByApiPathAndMethod(String apiPath, String method);
}