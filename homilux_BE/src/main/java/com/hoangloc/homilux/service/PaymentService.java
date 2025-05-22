package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.Event;
import com.hoangloc.homilux.domain.Payment;
import com.hoangloc.homilux.domain.dto.PaymentDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.EventRepository;
import com.hoangloc.homilux.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;

    public PaymentService(PaymentRepository paymentRepository, EventRepository eventRepository) {
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
    }

    public PaymentDto createPayment(Payment payment) {
        if (paymentRepository.existsByTransactionId(payment.getTransactionId())) {
            throw new ResourceAlreadyExistsException("Thanh toán", "mã thanh toán", payment.getTransactionId());
        }
        Event event = eventRepository.findById(payment.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", payment.getEvent().getId()));
        payment.setEvent(event);

        Payment savedPayment = paymentRepository.save(payment);
        return toDto(savedPayment);
    }

    public PaymentDto updatePayment(Payment updatedPayment) {
        if (updatedPayment.getId() == null) {
            throw new IllegalArgumentException("ID thanh toán không được để trống!");
        }
        Payment payment = paymentRepository.findById(updatedPayment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", updatedPayment.getId()));

        if (!payment.getTransactionId().equals(updatedPayment.getTransactionId()) &&
                paymentRepository.existsByTransactionId(updatedPayment.getTransactionId())) {
            throw new ResourceAlreadyExistsException("Thanh toán", "mã thanh toán", updatedPayment.getTransactionId());
        }

        if (updatedPayment.getEvent() != null) {
            Event event = eventRepository.findById(updatedPayment.getEvent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", updatedPayment.getEvent().getId()));
            payment.setEvent(event);
        }

        payment.setTransactionId(updatedPayment.getTransactionId());
        payment.setAmount(updatedPayment.getAmount());
        payment.setPaymentDate(updatedPayment.getPaymentDate());
        payment.setStatus(updatedPayment.getStatus());

        Payment savedPayment = paymentRepository.save(payment);
        return toDto(savedPayment);
    }

    public void deletePayment(Long id) {
        paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", id));
        paymentRepository.deleteById(id); // Triggers soft delete via @SQLDelete
    }

    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", id));
        return toDto(payment);
    }

    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getPaymentsByPaymentStatus(String paymentStatus) {
        return paymentRepository.findPaymentsByStatus(paymentStatus)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setEventId(payment.getEvent().getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}