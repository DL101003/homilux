package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.domain.EventType;
import com.hoangloc.homilux.domain.Service;
import com.hoangloc.homilux.domain.dto.ServiceDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.EventTypeRepository;
import com.hoangloc.homilux.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class HomiluxServiceService extends AbstractPaginationService<Service> {

    private final ServiceRepository serviceRepository;
    private final EventTypeRepository eventTypeRepository;

    public HomiluxServiceService(ServiceRepository serviceRepository, EventTypeRepository eventTypeRepository) {
        super(serviceRepository);
        this.serviceRepository = serviceRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    public ServiceDto createService(Service service) {
        EventType eventType = eventTypeRepository.findById(service.getEventType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loại sự kiện", "ID", service.getEventType().getId()));

        if (serviceRepository.existsByNameAndEventTypeId(service.getName(), eventType.getId())) {
            throw new ResourceAlreadyExistsException("Dịch vụ", "tên và loại sự kiện", service.getName() + ", " + eventType.getId());
        }

        service.setEventType(eventType);
        Service savedService = serviceRepository.save(service);
        return toDto(savedService);
    }

    public ServiceDto updateService(Service updatedService) {
        if (updatedService.getId() == null) {
            throw new IllegalArgumentException("ID dịch vụ không được để trống!");
        }
        Service service = serviceRepository.findById(updatedService.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ", "ID", updatedService.getId()));

        EventType eventType = service.getEventType();
        if (updatedService.getEventType() != null) {
            eventType = eventTypeRepository.findById(updatedService.getEventType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Loại sự kiện", "ID", updatedService.getEventType().getId()));
        }

        if (!service.getName().equals(updatedService.getName()) || !service.getEventType().getId().equals(eventType.getId())) {
            if (serviceRepository.existsByNameAndEventTypeId(updatedService.getName(), eventType.getId())) {
                throw new ResourceAlreadyExistsException("Dịch vụ", "tên và loại sự kiện", updatedService.getName() + ", " + eventType.getId());
            }
        }

        service.setName(updatedService.getName());
        service.setPrice(updatedService.getPrice());
        service.setEventType(eventType);

        Service savedService = serviceRepository.save(service);
        return toDto(savedService);
    }

    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ", "ID", id));
        if (!service.getEvents().isEmpty()) {
            throw new IllegalStateException("Không thể xóa dịch vụ đang được sử dụng bởi sự kiện!");
        }
        serviceRepository.deleteById(id); // Triggers soft delete via @SQLDelete
    }

    public ServiceDto getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ", "ID", id));
        return toDto(service);
    }

    private ServiceDto toDto(Service service) {
        ServiceDto dto = new ServiceDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setPrice(service.getPrice());
        dto.setEventTypeId(service.getEventType().getId());
        dto.setCreatedAt(service.getCreatedAt());
        dto.setUpdatedAt(service.getUpdatedAt());
        return dto;
    }
}