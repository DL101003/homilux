package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Service;
import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import com.hoangloc.homilux.domain.dto.ServiceDto;
import com.hoangloc.homilux.service.HomiluxServiceService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class HomiluxServiceController {

    private final HomiluxServiceService serviceService;

    public HomiluxServiceController(HomiluxServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping("/services")
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody Service service) {
        ServiceDto createdService = serviceService.createService(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @GetMapping("/services")
    public ResponseEntity<ResultPaginationDTO> getAllServices(@Filter Specification<Service> spec, Pageable pageable) {
        return ResponseEntity.ok(serviceService.getAll(spec, pageable));
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable Long id) {
        ServiceDto service = serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PutMapping("/services")
    public ResponseEntity<ServiceDto> updateService(@RequestBody Service service) {
        ServiceDto updatedService = serviceService.updateService(service);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}