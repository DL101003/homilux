package com.hoangloc.homilux.service;

import com.hoangloc.homilux.domain.Dish;
import com.hoangloc.homilux.domain.Event;
import com.hoangloc.homilux.domain.Review;
import com.hoangloc.homilux.domain.User;
import com.hoangloc.homilux.domain.dto.ReviewDto;
import com.hoangloc.homilux.exception.ResourceAlreadyExistsException;
import com.hoangloc.homilux.exception.ResourceNotFoundException;
import com.hoangloc.homilux.repository.DishRepository;
import com.hoangloc.homilux.repository.EventRepository;
import com.hoangloc.homilux.repository.ReviewRepository;
import com.hoangloc.homilux.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final DishRepository dishRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, EventRepository eventRepository, DishRepository dishRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.dishRepository = dishRepository;
    }

    public ReviewDto createReview(Review review) {
        User user = userRepository.findById(review.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "ID", review.getUser().getId()));
        Event event = eventRepository.findById(review.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sự kiện", "ID", review.getEvent().getId()));
        Dish dish = dishRepository.findById(review.getDish().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", review.getDish().getId()));

        if (reviewRepository.existsByUserIdAndEventIdAndDishId(user.getId(), event.getId(), dish.getId())) {
            throw new ResourceAlreadyExistsException("Đánh giá", "userId, eventId, dishId", user.getId() + ", " + event.getId() + ", " + dish.getId());
        }

        review.setUser(user);
        review.setEvent(event);
        review.setDish(dish);

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

        if (updatedReview.getDish() != null) {
            Dish dish = dishRepository.findById(updatedReview.getDish().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Món ăn", "ID", updatedReview.getDish().getId()));
            review.setDish(dish);
        }

        if (updatedReview.getUser() != null && updatedReview.getEvent() != null && updatedReview.getDish() != null) {
            if (!review.getUser().getId().equals(updatedReview.getUser().getId()) ||
                    !review.getEvent().getId().equals(updatedReview.getEvent().getId()) ||
                    !review.getDish().getId().equals(updatedReview.getDish().getId())) {
                if (reviewRepository.existsByUserIdAndEventIdAndDishId(
                        updatedReview.getUser().getId(), updatedReview.getEvent().getId(), updatedReview.getDish().getId())) {
                    throw new ResourceAlreadyExistsException("Đánh giá", "userId, eventId, dishId",
                            updatedReview.getUser().getId() + ", " + updatedReview.getEvent().getId() + ", " + updatedReview.getDish().getId());
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

    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewsByEventId(Long eventId) {
        return reviewRepository.findByEventId(eventId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewsByDishId(Long dishId) {
        return reviewRepository.findByDishId(dishId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setEventId(review.getEvent().getId());
        dto.setDishId(review.getDish().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}