package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.annotation.ApiMessage;
import com.hoangloc.homilux.domain.Booking;
import com.hoangloc.homilux.domain.dto.BookingCreateDto;
import com.hoangloc.homilux.domain.dto.BookingDto;
import com.hoangloc.homilux.domain.dto.BookingUpdateDto;
import com.hoangloc.homilux.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    @ApiMessage("Create a booking")
    public ResponseEntity<BookingCreateDto> createBooking(@Valid @RequestBody Booking booking) {
        BookingCreateDto createdBooking = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @GetMapping("/bookings")
    @ApiMessage("Get all bookings")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/{id}")
    @ApiMessage("Get a booking by id")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/bookings")
    @ApiMessage("Update a booking")
    public ResponseEntity<BookingUpdateDto> updateBooking(@RequestBody Booking booking) {
        BookingUpdateDto updatedBooking = bookingService.updateBooking(booking);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/bookings/{id}")
    @ApiMessage("Delete a booking by id")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}