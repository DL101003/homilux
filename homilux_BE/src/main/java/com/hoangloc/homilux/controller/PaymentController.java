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

import java.util.Map;

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

    @PostMapping("/payments/create-vnpay")
    public ResponseEntity<String> createVNPayPayment(@RequestParam Long eventId,
                                                     @RequestHeader(value = "X-Forwarded-For", defaultValue = "127.0.0.1") String ipAddress) throws Exception {
        String paymentUrl = paymentService.createVNPayPayment(eventId, ipAddress);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/payments/callback")
    public ResponseEntity<String> handleVNPayCallback(@RequestParam Map<String, String> params) throws Exception {
        try {
            boolean success = paymentService.handleVNPayCallback(params);
            return ResponseEntity.ok(success ? "SUCCESS" : "FAIL");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }
}