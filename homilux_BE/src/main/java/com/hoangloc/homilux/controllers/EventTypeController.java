package com.hoangloc.homilux.controllers;

import com.hoangloc.homilux.dtos.eventTypeDto.EventTypeRequest;
import com.hoangloc.homilux.dtos.eventTypeDto.EventTypeResponse;
import com.hoangloc.homilux.services.EventTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event-types")
@RequiredArgsConstructor
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @PostMapping
    public ResponseEntity<EventTypeResponse> create(@Valid @RequestBody EventTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventTypeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventTypeResponse> update(@PathVariable Long id, @Valid @RequestBody EventTypeRequest request) {
        return ResponseEntity.ok(eventTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventTypeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventTypeService.getEventTypeById(id));
    }

    @GetMapping
    public ResponseEntity<List<EventTypeResponse>> getAll() {
        return ResponseEntity.ok(eventTypeService.getAll());
    }
}