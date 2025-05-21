package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.EventType;
import com.hoangloc.homilux.domain.dto.EventTypeDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.EventTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    public EventTypeService(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    public EventTypeDto createEventType(EventType eventType) {
        if (eventTypeRepository.existsByName(eventType.getName())) {
            throw new ResourceAlreadyExistsException("Loại sự kiện", "typeName", eventType.getName());
        }
        EventType savedEventType = eventTypeRepository.save(eventType);
        return toDto(savedEventType);
    }

    public EventTypeDto updateEventType(EventType updatedEventType) {
        if (updatedEventType.getId() == null) {
            throw new IllegalArgumentException("ID loại sự kiện không được để trống!");
        }
        EventType eventType = eventTypeRepository.findById(updatedEventType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loại sự kiện", "ID", updatedEventType.getId()));

        if (!eventType.getName().equals(updatedEventType.getName()) &&
                eventTypeRepository.existsByName(updatedEventType.getName())) {
            throw new ResourceAlreadyExistsException("Loại sự kiện", "typeName", updatedEventType.getName());
        }

        eventType.setName(updatedEventType.getName());
        eventType.setDescription(updatedEventType.getDescription());

        EventType savedEventType = eventTypeRepository.save(eventType);
        return toDto(savedEventType);
    }

    public void deleteEventType(Long id) {
        eventTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loại sự kiện", "ID", id));
        eventTypeRepository.deleteById(id); // Triggers soft delete via @SQLDelete
    }

    public EventTypeDto getEventTypeById(Long id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loại sự kiện", "ID", id));
        return toDto(eventType);
    }

    public List<EventTypeDto> getAllEventTypes() {
        return eventTypeRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private EventTypeDto toDto(EventType eventType) {
        EventTypeDto dto = new EventTypeDto();
        dto.setId(eventType.getId());
        dto.setName(eventType.getName());
        dto.setDescription(eventType.getDescription());
        dto.setCreatedAt(eventType.getCreatedAt());
        dto.setUpdatedAt(eventType.getUpdatedAt());
        return dto;
    }
}