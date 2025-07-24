package com.hoangloc.homilux.controllers;

import com.hoangloc.homilux.dtos.ResultPaginationDto;
import com.hoangloc.homilux.dtos.bookingDto.BookingCreationRequest;
import com.hoangloc.homilux.dtos.bookingDto.BookingResponse;
import com.hoangloc.homilux.dtos.bookingDto.BookingStatusUpdateRequest;
import com.hoangloc.homilux.entities.Booking;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.repositories.UserRepository;
import com.hoangloc.homilux.services.BookingService;
import com.hoangloc.homilux.services.SecurityUtil;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<ResultPaginationDto> getMyBookings(Pageable pageable) {
        return ResponseEntity.ok(bookingService.getBookingsForCurrentUser(pageable));
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody @Valid BookingCreationRequest request) {
        String email = SecurityUtil.getCurrentUser();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        BookingResponse bookingResponse = bookingService.createBooking(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long id, @RequestBody @Valid BookingStatusUpdateRequest statusRequest) {
        return ResponseEntity.ok(bookingService.updateStatus(id, statusRequest.status()));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDto> getAllBookings(@Filter Specification<Booking> spec, Pageable pageable) {
        return ResponseEntity.ok(bookingService.getAll(spec, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}