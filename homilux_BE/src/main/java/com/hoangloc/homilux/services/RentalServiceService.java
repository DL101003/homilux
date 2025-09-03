package com.hoangloc.homilux.services;

import com.hoangloc.homilux.dtos.rentalServiceDto.RentalServiceRequest;
import com.hoangloc.homilux.dtos.rentalServiceDto.RentalServiceResponse;
import com.hoangloc.homilux.entities.RentalService;
import com.hoangloc.homilux.exceptions.DuplicateResourceException;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.RentalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceService {

    private final RentalServiceRepository rentalServiceRepository;

    public RentalServiceResponse createService(RentalServiceRequest request) {
        // Kiểm tra trùng lặp tên dịch vụ
        if (rentalServiceRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Service with name '" + request.name() + "' already exists.");
        }

        RentalService service = RentalService.builder()
                .name(request.name())
                .description(request.description())
                .type(request.type())
                .price(request.price())
                .isCustomizable(request.isCustomizable())
                .build();

        RentalService savedService = rentalServiceRepository.save(service);
        return toResponse(savedService);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "rentalServiceList", key = "'all'")
    public List<RentalServiceResponse> getAllServices() {
        return rentalServiceRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "rentalServiceById", key = "#id")
    public RentalServiceResponse getServiceById(Long id) {
        return rentalServiceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("RentalService", id));
    }

    public RentalServiceResponse updateService(Long id, RentalServiceRequest request) {
        RentalService service = rentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RentalService", id));

        // Cập nhật các trường
        service.setName(request.name());
        service.setDescription(request.description());
        service.setType(request.type());
        service.setPrice(request.price());
        service.setCustomizable(request.isCustomizable());

        RentalService updatedService = rentalServiceRepository.save(service);
        return toResponse(updatedService);
    }

    public void deleteService(Long id) {
        if (!rentalServiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("RentalService", id);
        }
        rentalServiceRepository.deleteById(id);
    }

    private RentalServiceResponse toResponse(RentalService service) {
        return new RentalServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getType(),
                service.getPrice(),
                service.isCustomizable()
        );
    }
}