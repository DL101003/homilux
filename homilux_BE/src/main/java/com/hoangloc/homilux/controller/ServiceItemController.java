package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.dto.ServiceItemCreateDto;
import com.hoangloc.homilux.domain.dto.ServiceItemDto;
import com.hoangloc.homilux.domain.dto.ServiceItemUpdateDto;
import com.hoangloc.homilux.service.ServiceItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    @PostMapping("/service-items")
    public ResponseEntity<ServiceItemCreateDto> createServiceItem(@Valid @RequestBody ServiceItem serviceItem) {
        ServiceItemCreateDto createdServiceItem = serviceItemService.createServiceItem(serviceItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdServiceItem);
    }

    @GetMapping("/service-items")
    public ResponseEntity<List<ServiceItemDto>> getAllServiceItems() {
        List<ServiceItemDto> serviceItems = serviceItemService.getAllServiceItems();
        return ResponseEntity.ok(serviceItems);
    }

    @GetMapping("/service-items/{id}")
    public ResponseEntity<ServiceItemDto> getServiceItemById(@PathVariable Long id) {
        ServiceItemDto serviceItem = serviceItemService.getServiceItemById(id);
        return ResponseEntity.ok(serviceItem);
    }

    @PutMapping("/service-items")
    public ResponseEntity<ServiceItemUpdateDto> updateServiceItem(@RequestBody ServiceItem serviceItem) {
        ServiceItemUpdateDto updatedServiceItem = serviceItemService.updateServiceItem(serviceItem);
        return ResponseEntity.ok(updatedServiceItem);
    }

    @DeleteMapping("/service-items/{id}")
    public ResponseEntity<Void> deleteServiceItem(@PathVariable Long id) {
        serviceItemService.deleteServiceItem(id);
        return ResponseEntity.noContent().build();
    }
}