package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {

    Collection<Dish> findByCategory(String category);

    boolean existsByName(String name);
}
