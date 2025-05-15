package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.Booking;
import com.hoangloc.homilux.domain.Payment;
import com.hoangloc.homilux.domain.dto.PaymentCreateDto;
import com.hoangloc.homilux.domain.dto.PaymentDto;
import com.hoangloc.homilux.domain.dto.PaymentUpdateDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.BookingRepository;
import com.hoangloc.homilux.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    public PaymentCreateDto createPayment(Payment payment) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(payment.getBooking().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Đặt lịch", "ID", payment.getBooking().getId()));
        if (paymentRepository.findByBookingIdAndDeletedFalse(booking.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Thanh toán", "đặt lịch ID", booking.getId());
        }
        payment.setBooking(booking);
        payment.setDeleted(false);
        Payment savedPayment = paymentRepository.save(payment);
        return toCreateDto(savedPayment);
    }

    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", id));
        return toDto(payment);
    }

    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAllByDeletedFalse()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PaymentUpdateDto updatePayment(Payment updatedPayment) {
        if (updatedPayment.getId() == null) {
            throw new IllegalArgumentException("ID thanh toán không được để trống!");
        }
        Payment payment = paymentRepository.findByIdAndDeletedFalse(updatedPayment.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", updatedPayment.getId()));
        if (updatedPayment.getBooking() != null && updatedPayment.getBooking().getId() != null) {
            Booking booking = bookingRepository.findByIdAndDeletedFalse(updatedPayment.getBooking().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Đặt lịch", "ID", updatedPayment.getBooking().getId()));
            if (!booking.getId().equals(payment.getBooking().getId()) &&
                    paymentRepository.findByBookingIdAndDeletedFalse(booking.getId()).isPresent()) {
                throw new ResourceAlreadyExistsException("Thanh toán", "đặt lịch ID", booking.getId());
            }
            payment.setBooking(booking);
        }
        if (updatedPayment.getAmount() != null) {
            payment.setAmount(updatedPayment.getAmount());
        }
        if (updatedPayment.getPaymentMethod() != null) {
            payment.setPaymentMethod(updatedPayment.getPaymentMethod());
        }
        if (updatedPayment.getPaymentStatus() != null) {
            payment.setPaymentStatus(updatedPayment.getPaymentStatus());
        }
        if (updatedPayment.getPaymentDate() != null) {
            payment.setPaymentDate(updatedPayment.getPaymentDate());
        }
        Payment savedPayment = paymentRepository.save(payment);
        return toUpdateDto(savedPayment);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thanh toán", "ID", id));
        payment.setDeleted(true);
        paymentRepository.save(payment);
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setPaymentStatus(payment.getPaymentStatus().name());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }

    private PaymentCreateDto toCreateDto(Payment payment) {
        PaymentCreateDto dto = new PaymentCreateDto();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setPaymentStatus(payment.getPaymentStatus().name());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    private PaymentUpdateDto toUpdateDto(Payment payment) {
        PaymentUpdateDto dto = new PaymentUpdateDto();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setPaymentStatus(payment.getPaymentStatus().name());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setUpdatedAt(payment.getUpdatedAt());
        return dto;
    }
}