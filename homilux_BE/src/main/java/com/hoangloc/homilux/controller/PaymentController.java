package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Payment;
import com.hoangloc.homilux.domain.dto.PaymentDto;
import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import com.hoangloc.homilux.service.PaymentService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody Payment payment) {
        PaymentDto createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @GetMapping("/payments")
    public ResponseEntity<ResultPaginationDTO> getAllPayments(@Filter Specification<Payment> spec, Pageable pageable) {
//        List<PaymentDto> payments = paymentStatus != null ? paymentService.getPaymentsByPaymentStatus(paymentStatus) : paymentService.getAllPayments();
        return ResponseEntity.ok(paymentService.getAll(spec, pageable));
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/payments")
    public ResponseEntity<PaymentDto> updatePayment(@RequestBody Payment payment) {
        PaymentDto updatedPayment = paymentService.updatePayment(payment);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}