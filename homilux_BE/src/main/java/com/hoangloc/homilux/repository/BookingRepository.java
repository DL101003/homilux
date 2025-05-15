package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdAndDeletedFalse(Long id);

    List<Booking> findAllByDeletedFalse();

    List<Booking> findByUserIdAndDeletedFalse(Long userId);

    List<Booking> findByEventDateAndDeletedFalse(Instant eventDate);

    boolean existsByEventDateAndLocationTypeAndDeletedFalse(Instant eventDate, String locationType);

    boolean existsByEventDateAndCustomLocationAddressAndDeletedFalse(Instant eventDate, String customLocationAddress);
}