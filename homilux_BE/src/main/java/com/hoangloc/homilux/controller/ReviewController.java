package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Review;
import com.hoangloc.homilux.domain.dto.ResultPaginationDTO;
import com.hoangloc.homilux.domain.dto.ReviewDto;
import com.hoangloc.homilux.service.ReviewService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody Review review, @RequestPart(value = "files", required = false) MultipartFile[] files) {
        ReviewDto createdReview = reviewService.createReview(review, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ResultPaginationDTO> getAllReviews(@Filter Specification<Review> spec, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAll(spec, pageable));
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
        ReviewDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/reviews")
    public ResponseEntity<ReviewDto> updateReview(@RequestBody Review review) {
        ReviewDto updatedReview = reviewService.updateReview(review);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}