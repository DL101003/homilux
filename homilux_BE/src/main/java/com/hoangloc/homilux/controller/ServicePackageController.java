package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.ServicePackage;
import com.hoangloc.homilux.domain.dto.ServicePackageCreateDto;
import com.hoangloc.homilux.domain.dto.ServicePackageDto;
import com.hoangloc.homilux.domain.dto.ServicePackageUpdateDto;
import com.hoangloc.homilux.service.ServicePackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ServicePackageController {

    private final ServicePackageService servicePackageService;

    public ServicePackageController(ServicePackageService servicePackageService) {
        this.servicePackageService = servicePackageService;
    }

    @PostMapping("/service-packages")
    public ResponseEntity<ServicePackageCreateDto> createServicePackage(@Valid @RequestBody ServicePackage servicePackage) {
        ServicePackageCreateDto createdServicePackage = servicePackageService.createServicePackage(servicePackage);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdServicePackage);
    }

    @GetMapping("/service-packages")
    public ResponseEntity<List<ServicePackageDto>> getAllServicePackages() {
        List<ServicePackageDto> servicePackages = servicePackageService.getAllServicePackages();
        return ResponseEntity.ok(servicePackages);
    }

    @GetMapping("/service-packages/{id}")
    public ResponseEntity<ServicePackageDto> getServicePackageById(@PathVariable Long id) {
        ServicePackageDto servicePackage = servicePackageService.getServicePackageById(id);
        return ResponseEntity.ok(servicePackage);
    }

    @PutMapping("/service-packages")
    public ResponseEntity<ServicePackageUpdateDto> updateServicePackage(@Valid @RequestBody ServicePackage servicePackage) {
        ServicePackageUpdateDto updatedServicePackage = servicePackageService.updateServicePackage(servicePackage);
        return ResponseEntity.ok(updatedServicePackage);
    }

    @DeleteMapping("/service-packages/{id}")
    public ResponseEntity<Void> deleteServicePackage(@PathVariable Long id) {
        servicePackageService.deleteServicePackage(id);
        return ResponseEntity.noContent().build();
    }
}