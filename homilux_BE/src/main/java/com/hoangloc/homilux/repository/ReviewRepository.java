package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    boolean existsByUserIdAndEventId(Long id, Long id1);

    long countByUserIdAndEventId(Long id, Long id1);
}