package com.hoangloc.homilux.repositories;

import com.hoangloc.homilux.entities.RentalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalServiceRepository extends JpaRepository<RentalService, Long> {
    boolean existsByName(String name);
}
