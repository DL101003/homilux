package com.hoangloc.homilux.controllers;

import com.hoangloc.homilux.dtos.paymentDto.PaymentRequest;
import com.hoangloc.homilux.dtos.paymentDto.PaymentResponse;
import com.hoangloc.homilux.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/bookings/{bookingId}/payments")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable Long bookingId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(bookingId, request));
    }

    @GetMapping("/bookings/{bookingId}/payments")
    public ResponseEntity<List<PaymentResponse>> getPaymentsForBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsForBooking(bookingId));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
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