package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.MenuItem;
import com.hoangloc.homilux.util.MenuItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByType(MenuItemType type);

    boolean existsByName(String name);
}
