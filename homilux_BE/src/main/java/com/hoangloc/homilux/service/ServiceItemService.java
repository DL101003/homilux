package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.ServiceItem;
import com.hoangloc.homilux.domain.dto.ServiceItemCreateDto;
import com.hoangloc.homilux.domain.dto.ServiceItemDto;
import com.hoangloc.homilux.domain.dto.ServiceItemUpdateDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemService(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    public ServiceItemCreateDto createServiceItem(ServiceItem serviceItem) {
        if (serviceItemRepository.existsByName(serviceItem.getName())) {
            throw new ResourceAlreadyExistsException("Mục dịch vụ", "tên", serviceItem.getName());
        }
        ServiceItem savedServiceItem = serviceItemRepository.save(serviceItem);
        return toCreateDto(savedServiceItem);
    }

    public ServiceItemDto getServiceItemById(Long id) {
        ServiceItem serviceItem = serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mục dịch vụ", "ID", id));
        return toDto(serviceItem);
    }

    public List<ServiceItemDto> getAllServiceItems() {
        return serviceItemRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ServiceItemUpdateDto updateServiceItem(ServiceItem updatedServiceItem) {
        if (updatedServiceItem.getId() == null) {
            throw new IllegalArgumentException("ID mục dịch vụ không được để trống!");
        }
        ServiceItem serviceItem = serviceItemRepository.findById(updatedServiceItem.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mục dịch vụ", "ID", updatedServiceItem.getId()));
        if (updatedServiceItem.getName() != null && !updatedServiceItem.getName().equals(serviceItem.getName()) &&
                serviceItemRepository.existsByName(updatedServiceItem.getName())) {
            throw new ResourceAlreadyExistsException("Mục dịch vụ", "tên", updatedServiceItem.getName());
        }
        if (updatedServiceItem.getName() != null) {
            serviceItem.setName(updatedServiceItem.getName());
        }
        if (updatedServiceItem.getDescription() != null) {
            serviceItem.setDescription(updatedServiceItem.getDescription());
        }
        if (updatedServiceItem.getEventType() != null) {
            serviceItem.setEventType(updatedServiceItem.getEventType());
        }
        if (updatedServiceItem.getPrice() != null) {
            serviceItem.setPrice(updatedServiceItem.getPrice());
        }
        serviceItem.setActive(updatedServiceItem.isActive());
        ServiceItem savedServiceItem = serviceItemRepository.save(serviceItem);
        return toUpdateDto(savedServiceItem);
    }

    public void deleteServiceItem(Long id) {
        ServiceItem serviceItem = serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mục dịch vụ", "ID", id));
        serviceItemRepository.save(serviceItem);
    }

    private ServiceItemDto toDto(ServiceItem serviceItem) {
        ServiceItemDto dto = new ServiceItemDto();
        dto.setId(serviceItem.getId());
        dto.setName(serviceItem.getName());
        dto.setDescription(serviceItem.getDescription());
        dto.setEventType(serviceItem.getEventType().name());
        dto.setPrice(serviceItem.getPrice());
        dto.setActive(serviceItem.isActive());
        dto.setCreatedAt(serviceItem.getCreatedAt());
        dto.setUpdatedAt(serviceItem.getUpdatedAt());
        return dto;
    }

    private ServiceItemCreateDto toCreateDto(ServiceItem serviceItem) {
        ServiceItemCreateDto dto = new ServiceItemCreateDto();
        dto.setId(serviceItem.getId());
        dto.setName(serviceItem.getName());
        dto.setDescription(serviceItem.getDescription());
        dto.setEventType(serviceItem.getEventType().name());
        dto.setPrice(serviceItem.getPrice());
        dto.setActive(serviceItem.isActive());
        dto.setCreatedAt(serviceItem.getCreatedAt());
        return dto;
    }

    private ServiceItemUpdateDto toUpdateDto(ServiceItem serviceItem) {
        ServiceItemUpdateDto dto = new ServiceItemUpdateDto();
        dto.setId(serviceItem.getId());
        dto.setName(serviceItem.getName());
        dto.setDescription(serviceItem.getDescription());
        dto.setEventType(serviceItem.getEventType().name());
        dto.setPrice(serviceItem.getPrice());
        dto.setActive(serviceItem.isActive());
        dto.setUpdatedAt(serviceItem.getUpdatedAt());
        return dto;
    }
}