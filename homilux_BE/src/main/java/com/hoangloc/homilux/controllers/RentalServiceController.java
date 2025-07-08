package com.hoangloc.homilux.controllers;

import com.hoangloc.homilux.dtos.rentalServiceDto.RentalServiceRequest;
import com.hoangloc.homilux.dtos.rentalServiceDto.RentalServiceResponse;
import com.hoangloc.homilux.entities.RentalService;
import com.hoangloc.homilux.services.RentalServiceService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class RentalServiceController {
    private final RentalServiceService rentalServiceService;

    @PostMapping
    public ResponseEntity<RentalServiceResponse> create(@Valid @RequestBody RentalServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalServiceService.createService(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalServiceResponse> update(@PathVariable Long id, @Valid @RequestBody RentalServiceRequest request) {
        return ResponseEntity.ok(rentalServiceService.updateService(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalServiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalServiceService.getServiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<RentalServiceResponse>> getAll() {
        return ResponseEntity.ok(rentalServiceService.getAllServices());
    }
}