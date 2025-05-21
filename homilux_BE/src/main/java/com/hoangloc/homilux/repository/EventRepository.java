package com.hoangloc.homilux.repository;

import com.hoangloc.homilux.domain.Event;
import com.hoangloc.homilux.util.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByPaymentStatus(PaymentStatus paymentStatus);
}
