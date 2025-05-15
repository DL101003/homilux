package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByIdAndDeletedFalse(Long id);

    List<Role> findAllByDeletedFalse();

    boolean existsByNameAndDeletedFalse(String name);
}