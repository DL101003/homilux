package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Event;
import com.hoangloc.homilux.domain.dto.EventDto;
import com.hoangloc.homilux.service.EventService;
import com.hoangloc.homilux.util.PaymentStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events")
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody Event event) {
        EventDto createdEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> getAllEvents(@RequestParam(required = false) PaymentStatus paymentStatus) {
        List<EventDto> events = paymentStatus != null ? eventService.getEventsByPaymentStatus(paymentStatus) : eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        EventDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/events")
    public ResponseEntity<EventDto> updateEvent(@RequestBody Event event) {
        EventDto updatedEvent = eventService.updateEvent(event);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}