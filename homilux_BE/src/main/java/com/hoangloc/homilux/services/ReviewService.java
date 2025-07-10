package com.hoangloc.homilux.services;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.dtos.reviewDto.ReviewImageResponse;
import com.hoangloc.homilux.dtos.reviewDto.ReviewRequest;
import com.hoangloc.homilux.dtos.reviewDto.ReviewResponse;
import com.hoangloc.homilux.dtos.userDto.UserResponse;
import com.hoangloc.homilux.entities.Booking;
import com.hoangloc.homilux.entities.Review;
import com.hoangloc.homilux.entities.ReviewImage;
import com.hoangloc.homilux.entities.User;
import com.hoangloc.homilux.entities.enums.BookingStatus;
import com.hoangloc.homilux.exceptions.DuplicateResourceException;
import com.hoangloc.homilux.exceptions.InvalidRequestException;
import com.hoangloc.homilux.exceptions.ResourceNotFoundException;
import com.hoangloc.homilux.repositories.BookingRepository;
import com.hoangloc.homilux.repositories.ReviewRepository;
import com.hoangloc.homilux.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ReviewService extends AbstractPaginationService<Review, ReviewResponse> {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository, UserRepository userRepository, FileService fileService) {
        super(reviewRepository);
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    public ReviewResponse createReview(Long bookingId, Long userId, ReviewRequest request, MultipartFile[] files) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        // Kiểm tra logic nghiệp vụ
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new InvalidRequestException("Cannot review a booking that is not completed.");
        }
        if (booking.getReview() != null) {
            throw new DuplicateResourceException("Booking already has a review.");
        }
        if (!booking.getUser().getId().equals(userId)) {
            throw new InvalidRequestException("You can only review your own bookings."); // Hoặc AccessDeniedException
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Review review = new Review();
        review.setBooking(booking);
        review.setUser(user);
        review.setRating(request.rating());
        review.setComment(request.comment());

        // Upload hình ảnh
        if (files != null && files.length > 0) {
            List<String> uploadResults = fileService.store(List.of(files), "reviews");
            for (String result : uploadResults) {
                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setReview(review);
                reviewImage.setImageUrl(result);
                review.getImages().add(reviewImage);
            }
        }

        return toResponse(reviewRepository.save(review));
    }

    @Override
    protected ReviewResponse toResponse(Review review) {
        Set<ReviewImageResponse> imageResponses = review.getImages() != null ?
                review.getImages().stream().map(this::toReviewImageResponse).collect(Collectors.toSet()) :
                Collections.emptySet();

        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getBooking().getId(),
                toUserSummaryResponse(review.getUser()),
                imageResponses,
                review.getCreatedAt()
        );
    }

    private ReviewImageResponse toReviewImageResponse(ReviewImage image) {
        return new ReviewImageResponse(image.getId(), image.getImageUrl());
    }

    private UserResponse toUserSummaryResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getAuthProvider(), user.getPhoneNumber(), user.getRole().getId(), user.getRole().getName());
    }


    @Transactional(readOnly = true)
    public ReviewResponse getReviewByBookingId(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        Review review = booking.getReview();
        if (review == null) {
            throw new ResourceNotFoundException("Review not found for booking: " + bookingId);
        }

        return toResponse(review);
    }
}