package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventDate(Instant eventDate);

    boolean existsByEventDateAndLocationType(Instant eventDate, String locationType);

    boolean existsByEventDateAndCustomLocationAddress(Instant eventDate, String customLocationAddress);
}