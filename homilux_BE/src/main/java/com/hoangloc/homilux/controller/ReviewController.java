package com.hoangloc.homilux.controller;

import com.hoangloc.homilux.domain.Review;
import com.hoangloc.homilux.domain.dto.ReviewDto;
import com.hoangloc.homilux.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody Review review) {
        ReviewDto createdReview = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> getAllReviews(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long dishId) {
        List<ReviewDto> reviews;
        if (eventId != null) {
            reviews = reviewService.getReviewsByEventId(eventId);
        } else if (dishId != null) {
            reviews = reviewService.getReviewsByDishId(dishId);
        } else {
            reviews = reviewService.getAllReviews();
        }
        return ResponseEntity.ok(reviews);
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