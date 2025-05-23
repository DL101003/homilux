package com.hoangloc.homilux.service;

import com.hoangloc.homilux.annotation.AbstractPaginationService;
import com.hoangloc.homilux.domain.*;
import com.hoangloc.homilux.domain.dto.ReviewDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.exception.StorageException;
import com.hoangloc.homilux.repository.DishRepository;
import com.hoangloc.homilux.repository.EventRepository;
import com.hoangloc.homilux.repository.ReviewRepository;
import com.hoangloc.homilux.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService extends AbstractPaginationService<Review, ReviewDto> {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final FileService fileService;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, EventRepository eventRepository, FileService fileService) {
        super(reviewRepository);
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.fileService = fileService;
    }

    public ReviewDto createReview(Review review, MultipartFile[] files) {
        User user = userRepository.findById(review.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", review.getUser().getId()));
        Event event = eventRepository.findById(review.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", review.getEvent().getId()));

        if (!event.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Chỉ người thuê dịch vụ được phép đánh giá!");
        }

        if (event.getEventDate().isAfter(Instant.now())) {
            throw new IllegalStateException("Chỉ có thể đánh giá sau khi sự kiện kết thúc!");
        }

        long reviewCount = reviewRepository.countByUserIdAndEventId(user.getId(), event.getId());
        if (reviewCount >= 3) {
            throw new IllegalStateException("Đã đạt giới hạn 3 đánh giá cho sự kiện này!");
        }

        review.setUser(user);
        review.setEvent(event);
        review.setImages(new ArrayList<>());

        // Upload hình ảnh
        if (files != null && files.length > 0) {
            List<String> uploadResults = fileService.store(List.of(files), "reviews");
            for (String result : uploadResults) {
                ReviewImage reviewImage = new ReviewImage();
                reviewImage.setReview(review);
                reviewImage.setImagePath(result);
                review.getImages().add(reviewImage);
            }
        }

        Review savedReview = reviewRepository.save(review);
        return toDto(savedReview);
    }

    public ReviewDto updateReview(Review updatedReview) {
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

        if (updatedReview.getEvent() != null) {
            Event event = eventRepository.findById(updatedReview.getEvent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", updatedReview.getEvent().getId()));
            review.setEvent(event);
        }

        if (updatedReview.getUser() != null && updatedReview.getEvent() != null) {
            if (!review.getUser().getId().equals(updatedReview.getUser().getId()) ||
                    !review.getEvent().getId().equals(updatedReview.getEvent().getId())) {
                if (reviewRepository.existsByUserIdAndEventId(
                        updatedReview.getUser().getId(), updatedReview.getEvent().getId())) {
                    throw new ResourceAlreadyExistsException("Đánh giá", "userId, eventId",
                            updatedReview.getUser().getId() + ", " + updatedReview.getEvent().getId());
                }
            }
        }

        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());

        Review savedReview = reviewRepository.save(review);
        return toDto(savedReview);
    }

    public void deleteReview(Long id) {
        reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá", "ID", id));
        reviewRepository.deleteById(id); // Triggers soft delete via @SQLDelete
    }

    public ReviewDto getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá", "ID", id));
        return toDto(review);
    }

//    public List<ReviewDto> getReviewsByEventId(Long eventId) {
//        return reviewRepository.findByEventId(eventId)
//                .stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }

//    public List<ReviewDto> getReviewsByDishId(Long dishId) {
//        return reviewRepository.findByDishId(dishId)
//                .stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }

    @Override
    protected ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setEventId(review.getEvent().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}