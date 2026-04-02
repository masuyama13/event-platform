package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Review;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.ReviewRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         OrganizerProfileRepository organizerProfileRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.organizerProfileRepository = organizerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review submitReview(Long userId, Long organizerId, String reviewText, Integer ratingValue) {

        if (reviewText == null || reviewText.trim().isEmpty()) {
            throw new RuntimeException("Review text cannot be empty");
        }

        if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        OrganizerProfile organizer = organizerProfileRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found: " + organizerId));

        Review review = reviewRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .orElse(new Review());

        review.setUser(user);
        review.setOrganizer(organizer);
        review.setReviewText(reviewText.trim());
        review.setRatingValue(ratingValue);

        Review savedReview = reviewRepository.save(review);
        updateAverageRating(organizer);
        return savedReview;
    }

    public List<Review> getReviewsByOrganizer(Long organizerId) {
        return reviewRepository.findByOrganizerIdOrderByCreatedAtDesc(organizerId);
    }

    public String getUserReviewText(Long userId, Long organizerId) {
        return reviewRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .map(Review::getReviewText)
                .orElse("");
    }

    public Integer getUserRating(Long userId, Long organizerId) {
        return reviewRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .map(Review::getRatingValue)
                .orElse(0);
    }

    private void updateAverageRating(OrganizerProfile organizer) {
        Double average = reviewRepository.findAverageRatingByOrganizerId(organizer.getId());
        double rounded = average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
        organizer.setAverageRating(rounded);
        organizerProfileRepository.save(organizer);
    }
}
