package com.hoangloc.homilux.repositories;

import com.hoangloc.homilux.entities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    boolean existsByName(String name);
}