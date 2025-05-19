package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.ServiceItem;
import com.hoangloc.homilux.domain.ServicePackage;
import com.hoangloc.homilux.domain.dto.ServicePackageCreateDto;
import com.hoangloc.homilux.domain.dto.ServicePackageDto;
import com.hoangloc.homilux.domain.dto.ServicePackageUpdateDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.ServiceItemRepository;
import com.hoangloc.homilux.repository.ServicePackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicePackageService {

    private final ServicePackageRepository servicePackageRepository;
    private final ServiceItemRepository serviceItemRepository;

    public ServicePackageService(ServicePackageRepository servicePackageRepository, ServiceItemRepository serviceItemRepository) {
        this.servicePackageRepository = servicePackageRepository;
        this.serviceItemRepository = serviceItemRepository;
    }

    public ServicePackageCreateDto createServicePackage(ServicePackage servicePackage) {
        if (servicePackageRepository.existsByName(servicePackage.getName())) {
            throw new ResourceAlreadyExistsException("Gói dịch vụ", "tên", servicePackage.getName());
        }
        List<ServiceItem> serviceItems = servicePackage.getServiceItems().stream()
                .map(item -> serviceItemRepository.findById(item.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Mục dịch vụ", "ID", item.getId())))
                .collect(Collectors.toList());
        servicePackage.setServiceItems(serviceItems);
        ServicePackage savedServicePackage = servicePackageRepository.save(servicePackage);
        return toCreateDto(savedServicePackage);
    }

    public ServicePackageDto getServicePackageById(Long id) {
        ServicePackage servicePackage = servicePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gói dịch vụ", "ID", id));
        return toDto(servicePackage);
    }

    public List<ServicePackageDto> getAllServicePackages() {
        return servicePackageRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ServicePackageUpdateDto updateServicePackage(ServicePackage updatedServicePackage) {
        if (updatedServicePackage.getId() == null) {
            throw new IllegalArgumentException("ID gói dịch vụ không được để trống!");
        }
        ServicePackage servicePackage = servicePackageRepository.findById(updatedServicePackage.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Gói dịch vụ", "ID", updatedServicePackage.getId()));
        if (updatedServicePackage.getName() != null && !updatedServicePackage.getName().equals(servicePackage.getName()) &&
                servicePackageRepository.existsByName(updatedServicePackage.getName())) {
            throw new ResourceAlreadyExistsException("Gói dịch vụ", "tên", updatedServicePackage.getName());
        }
        if (updatedServicePackage.getName() != null) {
            servicePackage.setName(updatedServicePackage.getName());
        }
        if (updatedServicePackage.getDescription() != null) {
            servicePackage.setDescription(updatedServicePackage.getDescription());
        }
        if (updatedServicePackage.getEventType() != null) {
            servicePackage.setEventType(updatedServicePackage.getEventType());
        }
        if (updatedServicePackage.getServiceItems() != null) {
            List<ServiceItem> serviceItems = updatedServicePackage.getServiceItems().stream()
                    .map(item -> serviceItemRepository.findById(item.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Mục dịch vụ", "ID", item.getId())))
                    .collect(Collectors.toList());
            servicePackage.setServiceItems(serviceItems);
        }
        if (updatedServicePackage.getPrice() != null) {
            servicePackage.setPrice(updatedServicePackage.getPrice());
        }
        servicePackage.setActive(updatedServicePackage.isActive());
        ServicePackage savedServicePackage = servicePackageRepository.save(servicePackage);
        return toUpdateDto(savedServicePackage);
    }

    public void deleteServicePackage(Long id) {
        ServicePackage servicePackage = servicePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gói dịch vụ", "ID", id));
        servicePackageRepository.save(servicePackage);
    }

    private ServicePackageDto toDto(ServicePackage servicePackage) {
        ServicePackageDto dto = new ServicePackageDto();
        dto.setId(servicePackage.getId());
        dto.setName(servicePackage.getName());
        dto.setDescription(servicePackage.getDescription());
        dto.setEventType(servicePackage.getEventType().name());
        dto.setServiceItemIds(servicePackage.getServiceItems().stream().map(ServiceItem::getId).collect(Collectors.toList()));
        dto.setPrice(servicePackage.getPrice());
        dto.setActive(servicePackage.isActive());
        dto.setCreatedAt(servicePackage.getCreatedAt());
        dto.setUpdatedAt(servicePackage.getUpdatedAt());
        return dto;
    }

    private ServicePackageCreateDto toCreateDto(ServicePackage servicePackage) {
        ServicePackageCreateDto dto = new ServicePackageCreateDto();
        dto.setId(servicePackage.getId());
        dto.setName(servicePackage.getName());
        dto.setDescription(servicePackage.getDescription());
        dto.setEventType(servicePackage.getEventType().name());
        dto.setServiceItemIds(servicePackage.getServiceItems().stream().map(ServiceItem::getId).collect(Collectors.toList()));
        dto.setPrice(servicePackage.getPrice());
        dto.setActive(servicePackage.isActive());
        dto.setCreatedAt(servicePackage.getCreatedAt());
        return dto;
    }

    private ServicePackageUpdateDto toUpdateDto(ServicePackage servicePackage) {
        ServicePackageUpdateDto dto = new ServicePackageUpdateDto();
        dto.setId(servicePackage.getId());
        dto.setName(servicePackage.getName());
        dto.setDescription(servicePackage.getDescription());
        dto.setEventType(servicePackage.getEventType().name());
        dto.setServiceItemIds(servicePackage.getServiceItems().stream().map(ServiceItem::getId).collect(Collectors.toList()));
        dto.setPrice(servicePackage.getPrice());
        dto.setActive(servicePackage.isActive());
        dto.setUpdatedAt(servicePackage.getUpdatedAt());
        return dto;
    }
}