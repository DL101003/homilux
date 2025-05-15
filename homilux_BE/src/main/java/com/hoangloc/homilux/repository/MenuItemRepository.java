package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Optional<MenuItem> findByIdAndDeletedFalse(Long id);
}
