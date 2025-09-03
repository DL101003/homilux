package com.hoangloc.homilux.services;

import com.hoangloc.homilux.dtos.eventTypeDto.EventTypeRequest;
import com.hoangloc.homilux.dtos.eventTypeDto.EventTypeResponse;
import com.hoangloc.homilux.entities.EventType;
import com.hoangloc.homilux.exceptions.DuplicateResourceException;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.EventTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    public EventTypeResponse create(EventTypeRequest request) {
        if (eventTypeRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("EventType with name '" + request.name() + "' already exists.");
        }
        EventType eventType = new EventType();
        eventType.setName(request.name());
        return toResponse(eventTypeRepository.save(eventType));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "eventTypeList", key = "'all'")
    public List<EventTypeResponse> getAll() {
        return eventTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "eventTypeList", allEntries = true),
            @CacheEvict(cacheNames = "eventTypeById", key = "#id")
    })
    public EventTypeResponse update(Long id, EventTypeRequest request) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventType", id));
        eventType.setName(request.name());
        return toResponse(eventTypeRepository.save(eventType));
    }

    public void delete(Long id) {
        if (!eventTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("EventType", id);
        }
        eventTypeRepository.deleteById(id);
    }

    private EventTypeResponse toResponse(EventType eventType) {
        return new EventTypeResponse(eventType.getId(), eventType.getName());
    }

    @Cacheable(cacheNames = "eventTypeById", key = "#id")
    public EventTypeResponse getEventTypeById(Long id) {
        return eventTypeRepository.findById(id).map(this::toResponse).
                orElseThrow(() -> new ResourceNotFoundException("EventType", id));
    }
}