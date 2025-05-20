package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ReviewCreateDto;
import com.hoangloc.homilux.domain.dto.ReviewDto;
import com.hoangloc.homilux.domain.dto.ReviewUpdateDto;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.ReviewRepository;
import com.hoangloc.homilux.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public ReviewCreateDto createReview(Review review) {
        User user = userRepository.findById(review.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", review.getUser().getId()));
        Booking booking = bookingRepository.findById(review.getBooking().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Đặt lịch", "ID", review.getBooking().getId()));
        review.setUser(user);
        review.setBooking(booking);
        Review savedReview = reviewRepository.save(review);
        return toCreateDto(savedReview);
    }

    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá", "ID", id));
        return toDto(review);
    }

    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ReviewUpdateDto updateReview(Review updatedReview) {
        if (updatedReview.getId() == null) {
            throw new IllegalArgumentException("ID đánh giá không được để trống!");
        }
        Review review = reviewRepository.findById(updatedReview.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá", "ID", updatedReview.getId()));
        if (updatedReview.getUser() != null) {
            User user = userRepository.findById(updatedReview.getUser().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", updatedReview.getUser().getId()));
            review.setUser(user);
        }
        if (updatedReview.getBooking() != null) {
            Booking booking = bookingRepository.findById(updatedReview.getBooking().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Đặt lịch", "ID", updatedReview.getBooking().getId()));
            review.setBooking(booking);
        }
        if (updatedReview.getRating() != null) {
            review.setRating(updatedReview.getRating());
        }
        if (updatedReview.getComment() != null) {
            review.setComment(updatedReview.getComment());
        }
        Review savedReview = reviewRepository.save(review);
        return toUpdateDto(savedReview);
    }

    public void deleteReview(Long id) {
        reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá", "ID", id));
        reviewRepository.deleteById(id);
    }

    private ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setBookingId(review.getBooking().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }

    private ReviewCreateDto toCreateDto(Review review) {
        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setBookingId(review.getBooking().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    private ReviewUpdateDto toUpdateDto(Review review) {
        ReviewUpdateDto dto = new ReviewUpdateDto();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setBookingId(review.getBooking().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}