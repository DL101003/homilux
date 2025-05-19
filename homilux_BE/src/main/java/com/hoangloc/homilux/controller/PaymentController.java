package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.annotation.ApiMessage;
import com.hoangloc.homilux.domain.Payment;
import com.hoangloc.homilux.domain.dto.PaymentCreateDto;
import com.hoangloc.homilux.domain.dto.PaymentDto;
import com.hoangloc.homilux.domain.dto.PaymentUpdateDto;
import com.hoangloc.homilux.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    @ApiMessage("Create a payment")
    public ResponseEntity<PaymentCreateDto> createPayment(@Valid @RequestBody Payment payment) {
        PaymentCreateDto createdPayment = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @GetMapping("/payments")
    @ApiMessage("Get all payments")
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/payments/{id}")
    @ApiMessage("Get a payment by id")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/payments")
    @ApiMessage("Update a payment")
    public ResponseEntity<PaymentUpdateDto> updatePayment(@RequestBody Payment payment) {
        PaymentUpdateDto updatedPayment = paymentService.updatePayment(payment);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/payments/{id}")
    @ApiMessage("Delete a payment by id")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}