package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.EventType;
import com.hoangloc.homilux.domain.dto.EventTypeDto;
import com.hoangloc.homilux.service.EventTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EventTypeController {

    private final EventTypeService eventTypeService;

    public EventTypeController(EventTypeService eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    @PostMapping("/event-types")
    public ResponseEntity<EventTypeDto> createEventType(@Valid @RequestBody EventType eventType) {
        EventTypeDto createdEventType = eventTypeService.createEventType(eventType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEventType);
    }

    @GetMapping("/event-types")
    public ResponseEntity<List<EventTypeDto>> getAllEventTypes() {
        List<EventTypeDto> eventTypes = eventTypeService.getAllEventTypes();
        return ResponseEntity.ok(eventTypes);
    }

    @GetMapping("/event-types/{id}")
    public ResponseEntity<EventTypeDto> getEventTypeById(@PathVariable Long id) {
        EventTypeDto eventType = eventTypeService.getEventTypeById(id);
        return ResponseEntity.ok(eventType);
    }

    @PutMapping("/event-types")
    public ResponseEntity<EventTypeDto> updateEventType(@RequestBody EventType eventType) {
        EventTypeDto updatedEventType = eventTypeService.updateEventType(eventType);
        return ResponseEntity.ok(updatedEventType);
    }

    @DeleteMapping("/event-types/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        eventTypeService.deleteEventType(id);
        return ResponseEntity.noContent().build();
    }
}