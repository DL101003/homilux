package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    List<Review> findByEventId(Long eventId);
    List<Review> findByDishId(Long dishId);
    boolean existsByUserIdAndEventIdAndDishId(Long userId, Long eventId, Long dishId);
}