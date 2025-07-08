package com.hoangloc.homilux.controllers;

import com.hoangloc.homilux.dtos.ResultPaginationDto;
import com.hoangloc.homilux.dtos.reviewDto.ReviewRequest;
import com.hoangloc.homilux.dtos.reviewDto.ReviewResponse;
import com.hoangloc.homilux.entities.Review;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.repositories.UserRepository;
import com.hoangloc.homilux.services.ReviewService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private final UserRepository userRepository;

    // Tạo review cho một booking đã hoàn thành
    @PostMapping("/bookings/{bookingId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookingId, @RequestBody @Valid ReviewRequest request, @RequestPart(value = "files", required = false) MultipartFile[] files) {
        String email = SecurityUtil.getCurrentUser();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        ReviewResponse response = reviewService.createReview(bookingId, user.getId(), request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lấy review của một booking
    @GetMapping("/bookings/{bookingId}/reviews")
    public ResponseEntity<ReviewResponse> getReviewForBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(reviewService.getReviewByBookingId(bookingId));
    }

    // Lấy tất cả review công khai
    @GetMapping("/reviews")
    public ResponseEntity<ResultPaginationDto> getAllPublicReviews(@Filter Specification<Review> spec, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAll(spec, pageable));
    }

}
